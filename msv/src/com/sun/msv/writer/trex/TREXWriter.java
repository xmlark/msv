/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.trex;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.datatype.DataType;
import com.sun.msv.reader.trex.TREXGrammarReader;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.datatype.DataTypeImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

/**
 * converts any Grammar into TREX XML representation through SAX2 events.
 * 
 * <h2>How it works</h2>
 * 
 * <p>
 *   {@link Grammar} object can be thought as a (possibly) cyclic graph
 *   made from {@link Expression}. For example, the following simple
 *   TREX pattern will be represented as following AGM.
 * </p>
 * <pre><xmp>
 * <grammar>
 *   <start name="X">
 *     <element name="foo">
 *       <choice>
 *         <string> abc </string>
 *         <ref name="Y" />
 *       </choice>
 *     </element>
 *   </start>
 *   <define name="Y">
 *     <element name="bar">
 *       <string> abc </string>
 *       <optional>
 *         <ref name="X" />
 *       </optional>
 *     </element>
 *   </define>
 * </grammar>
 * </xmp></pre>
 * <img src="doc-files/simpleAGM.gif" />
 * 
 * <p>
 *   Note that
 * </p>
 * <ul>
 *   <li>sub expressions are shared (see &lt;string&gt; expression).
 *   <li>there is a cycle in the graph.
 *   <li>several syntax elements are replaced by others
 *       (e.g., &lt;optional&gt;P&lt;/optional&gt; -&gt; &lt;choice&gt;&lt;empty/&gt;P&lt;/choice&gt;)
 * </ul>
 * 
 * <p>
 *   To write these expressions into TREX XML representation,
 *   we have to take care of cycles, since cyclic references cannot be written into
 *   XML without first cut it and use &lt;ref&gt;/&lt;define&gt; pair.
 * </p>
 * 
 * <p>
 *   First, this algorithm splits the grammar into <i>"islands"</i>.
 *   Island is a tree of expressions; it has a <i>head</i> expression
 *   and most importantly it doesn't contain any cycles in it. Member of an island
 *   can be always reached from its head.
 * </p>
 * <img src="doc-files/island.gif"/>
 * <p>
 *   TREXWriter will make every {@link ElementExp} and
 *   {@link ReferenceExp} a head of their own island. So each of them
 *   has their own island.
 * </p><p>
 *   It is guaranteed that this split will always give islands without inner cycles.
 *   Several islands can form a cycle, but one island can never have a cycle in it.
 *   This is because there is always at least one ElementExp in any cycle.
 * </p>
 * <img src="doc-files/island_before.gif" />
 * <p>
 *   Note that since expressions are shared, one expression can be
 *   a member of several islands (although this isn't depicted in the above figure.)
 * </p>
 * <p>
 *   Then, this algorithm merges some islands. For example, island E is
 *   referenced only once (from island D). This means that there is no need to
 *   give a name to this pattern. Instead, island E can simply written as a
 *   subordinate of island D.
 * </p><p>
 *   In other words, any island who is only referenced at most once is merged
 *   into its referer. This step makes the output more compact.
 * </p>
 * <img src="doc-files/island_merged.gif" />
 * <p>
 *   Next, TREXWriter assigns a name to each island. It tries to use the name of
 *   the head expression. If a head is anonymous ReferenceExp (ReferenceExp whose
 *   name field is <code>null</code>) or there is a name conflict, TREXWriter
 *   will add some suffix to make the name unique.
 * </p><p>
 *   Finally, each island is written as one named pattern under &lt;define&gt;
 *   element. All inter-island references are replaced by &lt;ref&gt; element.
 * </p>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXWriter {
	
	/**
	 * this class is used to wrap SAXException by RuntimeException.
	 * 
	 * we can't throw Exception from visitor, so it has to be wrapped
	 * by RuntimeException. This exception is catched outside of visitor
	 * and nested exception is re-thrown.
	 */
	protected static class SAXWrapper extends RuntimeException {
		SAXException e;
		SAXWrapper( SAXException e ) { this.e=e; }
	}
	

	public void write( Grammar g ) throws SAXException {
		// find a namespace URI that can be used as default "ns" attribute.
		write(g,sniffDefaultNs(g.getTopLevel()));
	}
	
	/**
	 * generates SAX2 events of the specified grammar.
	 * 
	 * @param defaultNs
	 *		if specified, this namespace URI is used as "ns" attribute
	 *		of grammar element. Can be null.
	 */
	public void write( Grammar g, String defaultNs ) throws SAXException {
		
		this.grammar = g;
		
		// collect all reachable ElementExps and ReferenceExps.
		final Set nodes = new HashSet();
		// ElementExps and ReferenceExps who are referenced more than once.
		final Set heads = new HashSet();
		
		g.getTopLevel().visit( new VisitorBase(){
			// VisitorBase class traverses expressions in depth-first order.
			// So this invokation traverses the all reachable expressions from
			// the top level expression.
			
			// Whenever visiting elements and RefExps, they are memorized
			// to identify head of islands.
			public void onElement( ElementExp exp ) {
				if(nodes.contains(exp)) {
					heads.add(exp);
					return;	// prevent infinite recursion.
				}
				nodes.add(exp);
				super.onElement(exp);
			}
			public void onRef( ReferenceExp exp ) {
				if(nodes.contains(exp)) {
					heads.add(exp);
					return;	// prevent infinite recursion.
				}
				nodes.add(exp);
				super.onRef(exp);
			}
		});
		
		// now heads contain all expressions that work as heads of islands.
		
		
		// create (name->RefExp) map while resolving name conflicts
		// 
		Map name2exp = new HashMap();
		{
			int cnt=0;	// use to name anonymous RefExp.
		
			Iterator itr = heads.iterator();
			while( itr.hasNext() ) {
				Expression exp = (Expression)itr.next();
				if( exp instanceof ReferenceExp ) {
					ReferenceExp rexp = (ReferenceExp)exp;
					if( rexp.name == null ) {
						// generate unique name
						while( name2exp.containsKey("anonymous"+cnt) )
							cnt++;
						name2exp.put( "anonymous"+cnt, exp );
					} else
					if( name2exp.containsKey(rexp.name) ) {
						// name conflict. try to add suffix.
						int i = 2;
						while( name2exp.containsKey(rexp.name+i) )
							i++;
						name2exp.put( rexp.name+i, exp );
					} else {
						// name of this RefExp can be directly used without modification.
						name2exp.put( rexp.name, exp );
					}
				}
				else
				if( exp instanceof ElementExp ) {
					ElementExp eexp = (ElementExp)exp;
					NameClass nc = eexp.getNameClass();
					
					if( nc instanceof SimpleNameClass
					 && !name2exp.containsKey( ((SimpleNameClass)nc).localName ) )
						name2exp.put( ((SimpleNameClass)nc).localName, exp );
					else {
						// generate unique name
						while( name2exp.containsKey("element"+cnt) )
							cnt++;
						name2exp.put( "element"+cnt, exp );
					}
				} else
					throw new Error();	// assertion failed.
					// it must be ElementExp or ReferenceExp.
			}
		}
		
		// then reverse name2ref to ref2name
		exp2name = new HashMap();
		{
			Iterator itr = name2exp.keySet().iterator();
			while( itr.hasNext() ) {
				String name = (String)itr.next();
				exp2name.put( name2exp.get(name), name );
			}
		}
		
		
		// generates SAX events
		try {
			handler.startDocument();
			handler.startPrefixMapping("",TREXGrammarReader.TREXNamespace);
			
			if( defaultNs!=null )
				start("grammar",new String[]{"ns",defaultNs});
			else
				start("grammar");
			
			this.defaultNs = defaultNs;
			
			{// write start pattern.
				start("start");
				writeIsland( g.getTopLevel() );
				end("start");
			}
			
			// write all named expressions
			Iterator itr = exp2name.keySet().iterator();
			while( itr.hasNext() ) {
				Expression exp = (Expression)itr.next();
				String name = (String)exp2name.get(exp);
				if( exp instanceof ReferenceExp )
					exp = ((ReferenceExp)exp).exp;
				start("define",new String[]{"name",name});
				writeIsland( exp );
				end("define");
			}
			
			end("grammar");
			handler.endDocument();
		} catch( SAXWrapper sw ) {
			throw sw.e;
		}
	}
	
	/**
	 * writes a bunch of expression into one tree.
	 */
	protected void writeIsland( Expression exp ) {
		// pattern writer will traverse the island and generates XML representation.
		if( exp instanceof ElementExp )
			patternWriter.writeElement( (ElementExp)exp );
		else
			patternWriter.visitUnary(exp);
	}
	
	
	/** Grammar object which we are writing. */
	protected Grammar grammar;
	
	/**
	 * map from ReferenceExp/ElementExp to its unique name.
	 * "unique name" is used to write/reference this ReferenceExp.
	 * ReferenceExps who are not in this list can be directly written into XML.
	 */
	protected Map exp2name;
	
	
	/**
	 * sniffs namespace URI that can be used as default 'ns' attribute
	 * from expression.
	 * 
	 * find an element or attribute, then use its namespace URI.
	 */
	protected String sniffDefaultNs( Expression exp ) {
		return (String)exp.visit( new TREXPatternVisitor(){
			public Object onElement( ElementExp exp ) {
				return sniff(exp.getNameClass());
			}
			public Object onAttribute( AttributeExp exp ) {
				return sniff(exp.nameClass);
			}
			protected String sniff(NameClass nc) {
				if( nc instanceof SimpleNameClass )
					return ((SimpleNameClass)nc).namespaceURI;
				else
					return null;
			}
			public Object onChoice( ChoiceExp exp ) {
				return onBinExp(exp);
			}
			public Object onSequence( SequenceExp exp ) {
				return onBinExp(exp);
			}
			public Object onInterleave( InterleavePattern exp ) {
				return onBinExp(exp);
			}
			public Object onConcur( ConcurPattern exp ) {
				return onBinExp(exp);
			}
			public Object onBinExp( BinaryExp exp ) {
				Object o = exp.exp1.visit(this);
				if(o==null)	o = exp.exp2.visit(this);
				return o;
			}
			public Object onMixed( MixedExp exp ) {
				return exp.exp.visit(this);
			}
			public Object onOneOrMore( OneOrMoreExp exp ) {
				return exp.exp.visit(this);
			}
			public Object onRef( ReferenceExp exp ) {
				return exp.exp.visit(this);
			}
			public Object onNullSet() {
				return null;
			}
			public Object onEpsilon() {
				return null;
			}
			public Object onAnyString() {
				return null;
			}
			public Object onTypedString( TypedStringExp exp ) {
				return null;
			}
		});
	}
	
	
	/**
	 * namespace URI currently implied through "ns" attribute propagation.
	 */
	protected String defaultNs;
	
	
	protected ContentHandler handler;
	/** this contentHandler will receive XML representation of TREX pattern. */
	public void setContentHandler( ContentHandler handler ) {
		this.handler = handler;
	}
	
	
	
// primitive write methods
//-----------------------------------------
	protected void element( String name ) {
		element( name, new String[0] );
	}
	protected void element( String name, String[] attributes ) {
		start(name,attributes);
		end(name);
	}
	protected void start( String name ) {
		start(name, new String[0] );
	}
	protected void start( String name, String[] attributes ) {
		AttributesImpl as = new AttributesImpl();
		for( int i=0; i<attributes.length/2; i+=2 )
			as.addAttribute( "", attributes[i], attributes[i], "", attributes[i+1] );
		try {
			handler.startElement( TREXGrammarReader.TREXNamespace, name, name, as );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	protected void end( String name ) {
		try {
			handler.endElement( TREXGrammarReader.TREXNamespace, name, name );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	protected void characters( String str ) {
		try {
			handler.characters( str.toCharArray(), 0, str.length() );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}

	
	
	protected TREXNameClassVisitor nameClassWriter = createNameClassWriter();
	protected TREXNameClassVisitor createNameClassWriter() {
		return new NameClassWriter();
	}
	protected PatternWriter patternWriter = createPatternWriter();
	protected PatternWriter createPatternWriter() {
		return new PatternWriter();
	}
	
	
	
	/** visits NameClass and writes its XML representation. */
	protected class NameClassWriter implements TREXNameClassVisitor {
		public Object onAnyName(AnyNameClass nc) {
			element("anyName");
			return null;
		}
		
		protected void startWithNs( String name, String ns ) {
			if( ns.equals(defaultNs) )
				start(name);
			else
				start(name, new String[]{"ns",ns});
		}
		
		public Object onSimple( SimpleNameClass nc ) {
			startWithNs( "name", nc.namespaceURI );
			characters(nc.localName);
			end("name");
			return null;
		}
		
		public Object onNsName( NamespaceNameClass nc ) {
			startWithNs( "nsName", nc.namespaceURI );
			end("nsName");
			return null;
		}
		
		public Object onNot( NotNameClass nc ) {
			start("not");
			nc.child.visit(this);
			end("not");
			return null;
		}
		
		public Object onChoice( ChoiceNameClass nc ) {
			start("choice");
			
			Stack s = new Stack();
			s.push(nc.nc1);
			s.push(nc.nc2);
			
			while(!s.empty()) {
				NameClass n = (NameClass)s.pop();
				if(n instanceof ChoiceNameClass ) {
					s.push( ((ChoiceNameClass)n).nc1 );
					s.push( ((ChoiceNameClass)n).nc2 );
					continue;
				}
				
				n.visit(this);
			}
			
			end("choice");
			return null;
		}
		
		public Object onDifference( DifferenceNameClass nc ) {
			start("difference");
			Stack s = new Stack();
			
			while( nc.nc1 instanceof DifferenceNameClass ) {
				s.push( nc.nc2 );
				nc = (DifferenceNameClass)nc.nc1;
			}
			
			nc.nc1.visit(this);
			while( !s.empty() )
				((NameClass)s.pop()).visit(this);
	
			end("difference");
			return null;
		}
	}
	
	
	/** visits Expression and writes its XML representation. */
	protected class PatternWriter implements TREXPatternVisitorVoid {
		
		public void onRef( ReferenceExp exp ) {
			String uniqueName = (String)exp2name.get(exp);
			if( uniqueName!=null )
				element("ref", new String[]{"name",uniqueName});
			else
				// this expression will not be written as a named pattern.
				exp.exp.visit(this);
		}
	
		public void onElement( ElementExp exp ) {
			String uniqueName = (String)exp2name.get(exp);
			if( uniqueName!=null ) {
				// this element will be written as a named pattern
				element("ref", new String[]{"name",uniqueName} );
				return;
			} else
				writeElement(exp);
		}
			
		public void writeElement( ElementExp exp ) {
			NameClass nc = exp.getNameClass();
			if( nc instanceof SimpleNameClass
			&&  ((SimpleNameClass)nc).namespaceURI.equals(defaultNs) )
				// we can use name attribute to simplify output.
				start("element",new String[]{"name",
					((SimpleNameClass)nc).localName} );
			else {
				start("element");
				exp.getNameClass().visit(nameClassWriter);
			}
			visitUnary(simplify(exp.contentModel));
			end("element");
		}
		
		/**
		 * remove unnecessary ReferenceExp from content model.
		 * this will sometimes makes content model smaller.
		 */
		public Expression simplify( Expression exp ) {
			return exp.visit( new TREXPatternCloner(grammar.getPool()){
				public Expression onRef( ReferenceExp exp ) {
					if( exp2name.containsKey(exp) )
						// this ReferenceExp will be written as a named pattern.
						return exp;
					else
						// bind contents
						return exp.exp.visit(this);
				}
				public Expression onElement( ElementExp exp ) {
					return exp;
				}
				public Expression onAttribute( AttributeExp exp ) {
					return exp;
				}
			});
		}
		
	
		public void onEpsilon() {
			element("empty");
		}
	
		public void onNullSet() {
			element("notAllowed");
		}
	
		public void onAnyString() {
			element("anyString");
		}
	
		public void onInterleave( InterleavePattern exp ) {
			visitBinExp("interleave", exp, InterleavePattern.class );
		}
	
		public void onConcur( ConcurPattern exp ) {
			visitBinExp("interleave", exp, ConcurPattern.class );
		}
	
		protected void onOptional( Expression exp ) {
			if( exp instanceof OneOrMoreExp ) {
				// (X+)? == X*
				onZeroOrMore((OneOrMoreExp)exp);
				return;
			}
			start("optional");
			visitUnary(exp);
			end("optional");
		}
		
		public void onChoice( ChoiceExp exp ) {
			// use optional instead of <choice> p <empty/> </choice>
			if( exp.exp1==Expression.epsilon ) {
				onOptional(exp.exp2);
				return;
			}
			if( exp.exp2==Expression.epsilon ) {
				onOptional(exp.exp1);
				return;
			}
			
			visitBinExp("choice", exp, ChoiceExp.class );
		}
	
		public void onSequence( SequenceExp exp ) {
			visitBinExp("group", exp, SequenceExp.class );
		}
	
		public void visitBinExp( String elementName, BinaryExp exp, Class type ) {
			// since AGM is binarized,
			// <choice> a b c </choice> is represented as
			// <choice> a <choice> b c </choice></choice>
			// this method print them as <choice> a b c </choice>
			start(elementName);
			while(true) {
				exp.exp1.visit(this);
				if(exp.exp2.getClass()==type) {
					exp = (BinaryExp)exp.exp2;
					continue;
				}
				break;
			}
			exp.exp2.visit(this);
			end(elementName);
		}
	
		public void onMixed( MixedExp exp ) {
			start("mixed");
			visitUnary(exp.exp);
			end("mixed");
		}
	
		public void onOneOrMore( OneOrMoreExp exp ) {
			start("oneOrMore");
			visitUnary(exp.exp);
			end("oneOrMore");
		}
	
		protected void onZeroOrMore( OneOrMoreExp exp ) {
			// note that this method is not a member of TREXPatternVisitor.
			start("zeroOrMore");
			visitUnary(exp.exp);
			end("zeroOrMore");
		}
	
		public void onAttribute( AttributeExp exp ) {
			if( exp.nameClass instanceof SimpleNameClass
			&&  ((SimpleNameClass)exp.nameClass).namespaceURI.equals("") )
				// we can use name attribute.
				start("attribute", new String[]{"name",
					((SimpleNameClass)exp.nameClass).localName} );
			else {
				start("attribute");
				exp.nameClass.visit(nameClassWriter);
			}
			if( exp.exp != Expression.anyString )
				// we can omit <anyString/> in the attribute.
				visitUnary(exp.exp);
			end("attribute");
		}
		
		/**
		 * print expression but surpress unnecessary sequence.
		 */
		public void visitUnary( Expression exp ) {
			// TREX treats <zeroOrMore> p q </zeroOrMore>
			// as <zeroOrMore><group> p q </group></zeroOrMore>
			// This method tries to exploit this capability to
			// simplify output.
			if( exp instanceof SequenceExp ) {
				SequenceExp seq = (SequenceExp)exp;
				seq.exp1.visit(this);
				visitUnary(seq.exp2);
			}
			else
				exp.visit(this);
		}
		
		public void onTypedString( TypedStringExp exp ) {
			try {
				DataType dt = exp.dt;
				if( dt instanceof TypedString ) {
					TypedString ts = (TypedString)dt;
					if( ts.preserveWhiteSpace )
						start("string",new String[]{"whiteSpace","preserve"});
					else
						start("string");
					
					characters( ts.value );
					
					end("string");
					return;
				}
			
				if( dt instanceof DataTypeImpl ) {
					handler.startPrefixMapping("xsd",XSDVocabulary.XMLSchemaNamespace);
					// TODO: support serialization of derived types.
					element("data",new String[]{"type","xsd:"+dt.getName()});
					handler.endPrefixMapping("xsd");
					return;
				}
			
				throw new UnsupportedOperationException( dt.getClass().getName() );
			} catch( SAXException e ) {
				throw new SAXWrapper(e);
			}
		}
	}
}
