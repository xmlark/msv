/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.sm;

import com.sun.tahiti.compiler.Symbolizer;
import com.sun.tahiti.compiler.XMLWriter;
import com.sun.tahiti.grammar.*;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.reader.GrammarReaderController;
import org.xml.sax.SAXException;
import org.apache.xml.serialize.XMLSerializer;
import org.apache.xml.serialize.OutputFormat;
import java.io.ByteArrayOutputStream;
import java.util.Set;
import java.util.Iterator;

/**
 * generates a simple marhsllaer for a ClassItem.
 * 
 * <p>
 * This algorithm cannot generate a marshaller for complex object models.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MarshallerGenerator implements ExpressionVisitorVoid {

	/**
	 * generates a marshaller for the given ClassItem.
	 * 
	 * <p>
	 * Using this method directly is discouraged due to the following reasons.
	 * 
	 * <ul>
	 *  <li>
	 *		This method does NOT call the startDocument/endDocument events
	 *		of the handler. The caller has to invoke them if necessary.
	 *	</li><li>
	 *		This method could fail even after several fragments are sent
	 *		to a XMLWriter object, by throwing an Abort exception.
	 *		This happens when the object model is
	 *		too compilcated for this algorithm. The caller has to catch it
	 *		and cancel any sideeffects caused by already written fragments.
	 *	</li>
	 * </ul>
	 * 
	 * <p>
	 * This method throws an Abort exception if it finds impossible to generate
	 * a marshaller.
	 */
	public static void write( Symbolizer symbolizer, ClassItem cls, XMLWriter out,
				final GrammarReaderController controller ) {
		
		cls.exp.visit(new MarshallerGenerator(symbolizer,cls,out,controller));
	}

	/**
	 * generates a marshaller for the given ClassItem.
	 * 
	 * @return
	 *		a byte array that contains XML representation of the marshaller.
	 *		null if this method fails to produce a marhsaller.
	 */
	public static byte[] write( Symbolizer symbolizer, ClassItem cls,
					GrammarReaderController controller ) throws SAXException {
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
			final XMLWriter out = new XMLWriter(
				new XMLSerializer(baos,new OutputFormat("xml",null,true) ));
			
			out.handler.startDocument();
			write( symbolizer, cls, out, controller );
			out.handler.endDocument();
			
			return baos.toByteArray();
		} catch ( XMLWriter.SAXWrapper w ) {
			throw w.e;
		} catch ( Abort a ) {
			return null;
		}
	}
	
	
	
	private MarshallerGenerator( Symbolizer symbolizer,
		ClassItem cls, XMLWriter out, GrammarReaderController controller) {
		
		this.symbolizer = symbolizer;
		this.owner = cls;
		this.out = out;
		this.controller = controller;
	}
	
	private final Symbolizer symbolizer;
	private final ClassItem owner;
	private final XMLWriter out;
	private final GrammarReaderController controller;
	
	/**
	 * Exception that indicates the object model is too complicated
	 * to generate a marshaller by this algorithm.
	 */
	public static class Abort extends RuntimeException {};
	

	
	/**
	 * &lt;oneOrMore/> will produce the following marshaller.
	 * 
	 * <PRE>
	 * do {
	 *   marshal body;
	 * } while( item available );
	 * </PRE>
	 */
	public void onOneOrMore( OneOrMoreExp exp ) {
				
		Set uniqueFields = calcUniqueField( owner.exp, exp );
		if( uniqueFields==null ) {
			// this class cannot be marshalled.
			controller.warning( null,
				localize(
					UNABLE_TO_PRODUCE_MARSHALLER_ONEORMORE, owner.getTypeName() ));
			throw new Abort();
		}
		if( uniqueFields.size()==0 ) {
			// there is no field in this.
			// so ignore this <oneOrMore>.
			exp.exp.visit(this);
			return;
		}
				
		
		out.start("oneOrMore",new String[]{"while",setToString(uniqueFields)});
		exp.exp.visit(this);
		out.end("oneOrMore");
	}
				
	public void onChoice( ChoiceExp exp ) {
		Expression[] children = exp.getChildren();
		Expression otherwise = null;
		Expression undecidable = null;
					
		if( currentField!=null ) {
			/*
			if
				"this choice is contained in a FieldItem"
			and
			(
				(
					"the multiplicity of this field in this ClassItem is at most one"
						and
					"all the branches are either JavaItem or epsilon"
				)
					or
				"all the branches are JavaItem"
			)
			
			then we can produce a simple marshaller.
			
			this works for choices of <value>s/<data>s.
			*/
			boolean hasEpsilonEdge = false;
			boolean fail = false;
			boolean hasMarshallableItem = false;
			boolean hasPrimitiveItem = false;
			// common type of primitives
			PrimitiveItem primitiveType = null;
			
			for( int i=0; i<children.length; i++ ) {
				if( children[i] instanceof InterfaceItem
				||  children[i] instanceof ClassItem) {
					hasMarshallableItem = true;
					continue;
				}
				if( children[i] instanceof PrimitiveItem) {
					hasPrimitiveItem = true;
					PrimitiveItem thisType = (PrimitiveItem)children[i];
					if(primitiveType==null)		primitiveType = thisType;
					else
					// this short cut doesn't work if the underlying datatype is
					// possibly different.
					if(primitiveType.dt!=thisType.dt)	fail = true;
					continue;
				}
				
				if( (children[i] instanceof IgnoreItem && children[i].isEpsilonReducible() )
				||  children[i]==Expression.epsilon ) {
					hasEpsilonEdge = true;
					continue;
				}
				
				// otherwise, this simple short cut doesn't work
				fail = true;
				break;
			}
			
			if( hasMarshallableItem && hasPrimitiveItem )
				// this short cut doesn't work if these two types are mixed.
				fail = true;
			
			if( !fail && hasEpsilonEdge
			&&  !owner.getFieldUse(currentField.name).multiplicity.isAtMostOnce() )
				// if there is an epsilon-edge, then the entire field must be (0,1)/(1,1)
				// multiplicity.
				fail = true;
			
			if(!fail) {
				// all check done. the short-cut can be used.
				if( hasEpsilonEdge ) {
					out.start("choice");
					out.start("option",new String[]{"if",currentField.name});
				}
				
				if( hasMarshallableItem ) {
					out.element("marshall", new String[]{
							"type", "object",
							"fieldName", currentField.name });
				} else {
					out.element("marshall", new String[]{
							"type", "primitive",
							"fieldName", currentField.name,
							"dataSymbol", symbolizer.getId(primitiveType.exp) });
				}
				
				if( hasEpsilonEdge ) {
					out.end("option");
					out.start("otherwise");
					out.element("epsilon");
					out.end("otherwise");
					out.end("choice");
				}
				
				return;
			}
		}
		
			
			
		out.start("choice");
		
		for( int i=0; i<children.length; i++ ) {
			Set uniqueFields = calcUniqueField( owner.exp, children[i] );
						
			if( uniqueFields==null ) {
				// we can possibly have one branch which doesn't have
				// the unique field. ("possibly" means if there is
				// no "otherwise" branch.)
				if(undecidable!=null) {
					// unable to produce marshaller.
					controller.warning( null,
						localize(
							UNABLE_TO_PRODUCE_MARSHALLER_UNDECIDABLE_CHOICE, owner.getTypeName() ));
					throw new Abort();
				}
				undecidable = children[i];
				continue;
			}
			if( uniqueFields.size()==0 ) {
				// if this branch doesn't have a FieldItem, use it
				// as the "otherwise" case.
				otherwise = children[i];
				continue;
			}
						
			out.start("option",new String[]{"if",setToString(uniqueFields)});
			children[i].visit(this);
			out.end("option");
		}
		if( otherwise!=null && undecidable!=null ) {
			// we have both the "otherwise" branch and the "undecidable" branch.
			// we can't tell which branch to use, so we cannot produce a marshaller
			controller.warning( null,
				localize(
					UNABLE_TO_PRODUCE_MARSHALLER_MULTIPLE_DEFAULTS, owner.getTypeName() ));
			throw new Abort();
		}
					
		// we can have one undecidable branch or one "otherwise" branch.
		// this branch should be expanded if any other choices fail.
		out.start("otherwise");
		if(otherwise!=null)		otherwise.visit(this);
		else
		if(undecidable!=null)	undecidable.visit(this);
		else
			out.element("notPossible");
		out.end("otherwise");
						
		out.end("choice");
	}
	
	/**
	 * converts a set of Strings into whitespace-delimited string.
	 */			
	private String setToString( Set s ) {
		StringBuffer buffer = new StringBuffer();
		Iterator itr = s.iterator();
		while( itr.hasNext() ) {
			buffer.append(' ');
			buffer.append(itr.next());
		}
		return buffer.toString();
	}
				
				
				
	public void onKey( KeyExp exp ) {
		// ignore the identity constraint.
		// TODO: how do we ensure that the generated document
		// is OK wrt the key/keyref constraint.
		exp.exp.visit(this);
	}
				
	public void onEpsilon() {
		out.element("epsilon");
	}
				
	/**
	 * If there is a FieldItem that contains the currently visited expression,
	 * then this field holds a reference to that object. Otherwise set to null.
	 */
	private FieldItem currentField = null;
				
	public void onOther( OtherExp exp ) {
		if( exp instanceof FieldItem ) {
			// in properly normalized AGM, it can never be possible
			// for two FieldItems to nest.
			assert( currentField==null );
			currentField = (FieldItem)exp;
			exp.exp.visit(this);
			assert( currentField==exp );
			currentField = null;
			return;
		}
		if( exp instanceof ClassItem || exp instanceof InterfaceItem ) {
			out.element("marshall",
				new String[]{
					"type","object",
					"fieldName", currentField.name });
			return;
		}
		if( exp instanceof PrimitiveItem ) {
			out.element("marshall",
				new String[]{
					"type","primitive",
					"fieldName", currentField.name,
					"dataSymbol", symbolizer.getId(((PrimitiveItem)exp).exp) });
			return;
		}
		if( exp instanceof IgnoreItem ) {
			if( !exp.exp.isEpsilonReducible() ) {
				// if an IgnoreItem is not epsilon-reducible,
				// then apparently we cannot marshall this class.
				controller.warning( null,
					localize(
						UNABLE_TO_PRODUCE_MARSHALLER_IGNOREITEM, owner.getTypeName() ));
				throw new Abort();
			} else
				out.element("epsilon");
						
			return;
		}
					
		throw new Error();
	}
				
	public void onSequence( SequenceExp exp )		{ onGroup(exp); }
	public void onInterleave( InterleaveExp exp )	{ onGroup(exp); }
	public void onGroup( BinaryExp exp ) {
		out.start("group");
		Expression[] children = exp.getChildren();
		for( int i=0; i<children.length; i++ )
			children[i].visit(this);
		out.end("group");
	}
				
	public void onAttribute( AttributeExp exp ) {
		// TODO: support non-simple name class.
		if(!(exp.nameClass instanceof SimpleNameClass))
			throw new Error();
		
		onItem( "attribute", (SimpleNameClass)exp.nameClass, exp.exp );
	}
				
	public void onElement( ElementExp exp ) {
		// TODO: support non-simple name class.
		if(!(exp.getNameClass() instanceof SimpleNameClass))
			throw new Error();
		onItem( "element", (SimpleNameClass)exp.getNameClass(), exp.contentModel );
	}
				
	public void onItem( String tag, SimpleNameClass nc, Expression body ) {
		out.start(tag,
			new String[]{"uri", nc.namespaceURI, "name", nc.localName});
		body.visit(this);
		out.end(tag);
	}
				
	public void onList( ListExp exp ) {
		out.start("list");
		exp.exp.visit(this);
		out.end("list");
	}
				
				
	// expressions that do not affect the marshaller.
	public void onRef( ReferenceExp exp ) {
		exp.exp.visit(this);
	}
				
	// these methods shall never be called
	public void onMixed( MixedExp exp ) {
		throw new Error();
	}
	public void onConcur( ConcurExp exp ) {
		throw new Error();
	}
	public void onAnyString() {
		throw new Error();
	}
	public void onNullSet() {
		throw new Error();
	}
	public void onTypedString( TypedStringExp exp ) {
		// A TypedStringExp should have been wrapped by a PrimitiveItem.
		throw new Error();
	}
	
	
	
	/**
	 * computes the field name which can only appear in the specified branch
	 * among the entire tree starting from the 'root' node.
	 * 
	 * <p>
	 * use caution. Since Expressions are shared, it is possible that the root
	 * exp contains the branch more than once. For example,
	 * 
	 * <pre>
	 * root -> SequenceExp(branch,branch)
	 * </pre>
	 * 
	 * @return
	 *		this method returns null to indicate that there is no unique name.
	 *		Otherwise, this method returns a set
	 *		of the fields which appear in this branch but not elsewhere.
	 */
	private Set calcUniqueField( Expression root, final Expression branch ) {

		final Set branchFields = new java.util.HashSet();
		
		branch.visit( new FieldWalker(currentField){
			// this marshaller cannot support AGM that contains a loop
			protected void findField( FieldItem field, Type child ) {
				branchFields.add( field.name );
			}
		});

		root.visit( new ExpressionVisitorVoid(){
			private boolean visitBranch = false;
			private FieldItem currentField = null;

			public void onOther( OtherExp exp ) {
				if(!test(exp))	return;
				
				if( exp instanceof FieldItem ) {
					assert( currentField==null );
					currentField = (FieldItem)exp;
					exp.exp.visit(this);
					assert( currentField==exp );
					currentField = null;
					return;
				}
				if( exp instanceof ClassItem || exp instanceof InterfaceItem
				||  exp instanceof PrimitiveItem ) {
					assert( currentField!=null );
					branchFields.remove( currentField.name );
					return;
				}
				if( exp instanceof IgnoreItem )	return;
				assert(!(exp instanceof JavaItem));
				exp.exp.visit(this);
			}
			public void onChoice( ChoiceExp exp )		{ onBinExp(exp); }
			public void onSequence( SequenceExp exp )	{ onBinExp(exp); }
			public void onInterleave( InterleaveExp exp){ onBinExp(exp); }
			public void onBinExp( BinaryExp exp ) {
				if(test(exp)) {
					exp.exp1.visit(this);
					exp.exp2.visit(this);
				}
			}
			
			public void onElement( ElementExp exp ) {
				if(test(exp))	exp.contentModel.visit(this);
			}
			public void onAttribute( AttributeExp exp ) {
				if(test(exp))	exp.exp.visit(this);
			}
			public void onRef( ReferenceExp exp ) {
				if(test(exp))	exp.exp.visit(this);
			}
			public void onOneOrMore( OneOrMoreExp exp ) {
				if(test(exp))	exp.exp.visit(this);
			}
			public void onList( ListExp exp ) {
				if(test(exp))	exp.exp.visit(this);
			}
			public void onKey( KeyExp exp ) {
				if(test(exp))	exp.exp.visit(this);
			}
			public void onEpsilon() {}
			
			private boolean test( Expression exp ) {
				if( exp!=branch )	return true;
				
				if(visitBranch)
					// this is the second time to visit the branch.
					// that means this branch is contained more than once.
					// so there is no unique field.
					branchFields.clear();
					// by setting branchFields empty, the calcUniqueField method
					// will return null.
				else
					visitBranch = true;
				return false;
			}
			
				
			
			// these primitives should not be used.
			public void onMixed( MixedExp exp ) { throw new Error(); }
			public void onConcur( ConcurExp exp ) { throw new Error(); }
			public void onTypedString( TypedStringExp exp ) { throw new Error(); }
			public void onNullSet() { throw new Error(); }
			public void onAnyString() { throw new Error(); }
		});
		
		
		// there is no unique field.
		if( branchFields.size()==0 )	return null;
		
		return branchFields;
	}
	
	
	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}


	private static String localize( String prop, Object arg ) {
		return localize( prop, new Object[]{arg} );
	}

	private static String localize( String prop, Object[] args ) {
		String format = java.util.ResourceBundle.getBundle(
			"com.sun.tahiti.compiler.sm.Messages").getString(prop);
		
	    return java.text.MessageFormat.format(format, args );
	}

	static final String UNABLE_TO_PRODUCE_MARSHALLER_ONEORMORE = // arg:1
		"MarshallerGenerator.OneOrMore";
	static final String UNABLE_TO_PRODUCE_MARSHALLER_UNDECIDABLE_CHOICE = // arg:1
		"MarshallerGenerator.UndecidableChoice";
	static final String UNABLE_TO_PRODUCE_MARSHALLER_MULTIPLE_DEFAULTS = // arg:1
		"MarshallerGenerator.MultipleDefaults";
	static final String UNABLE_TO_PRODUCE_MARSHALLER_IGNOREITEM = // arg:1
		"Marshaller.IgnoreItem";
}
