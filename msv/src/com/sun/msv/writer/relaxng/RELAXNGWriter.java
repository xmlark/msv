/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.writer.relaxng;

import org.relaxng.datatype.*;
import org.relaxng.datatype.Datatype;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.msv.grammar.trex.TypedString;
import com.sun.msv.grammar.relaxng.ValueType;
import com.sun.msv.grammar.util.PossibleNamesCollector;
import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.*;
import com.sun.msv.reader.trex.ng.RELAXNGReader;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import com.sun.msv.writer.*;
import com.sun.msv.util.StringPair;
import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;
import org.xml.sax.helpers.LocatorImpl;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

/**
 * converts any Grammar into RELAX NG XML representation through SAX1 events.
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
 * <h2>Why SAX1?</h2>
 * <p>
 *   Due to the bug and insufficient supports for the serialization through SAX2,
 *   The decision is made to use SAX1. SAX1 allows us to control namespace prefix
 *   mappings better than SAX2.
 * </p>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGWriter implements GrammarWriter {
	
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
	 * 
	 * @exception IllegalArgumentException
	 *		If the given grammar is beyond the expressive power of TREX
	 *		(e.g., some RELAX NG grammar), then this exception is thrown.
	 */
	public void write( Grammar g, String defaultNs ) throws SAXException {
		
		this.grammar = g;
		
		// collect all reachable ElementExps and ReferenceExps.
		final Set nodes = new HashSet();
		// ElementExps and ReferenceExps who are referenced more than once.
		final Set heads = new HashSet();
		
		g.getTopLevel().visit( new ExpressionWalker(){
			// ExpressionWalker class traverses expressions in depth-first order.
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
			handler.setDocumentLocator( new LocatorImpl() );
			handler.startDocument();
//			handler.startPrefixMapping("",TREXGrammarReader.TREXNamespace);
//			handler.startPrefixMapping("trex",TREXGrammarReader.TREXNamespace);
//			handler.startPrefixMapping("xsd",XSDVocabulary.XMLSchemaNamespace);

			// to work around the bug of current serializer,
			// report xmlns declarations as attributes.
			
			if( defaultNs!=null )
				start("grammar",new String[]{
					"ns",defaultNs,
					"xmlns",RELAXNGReader.RELAXNGNamespace,
					"datatypeLibrary", XSDVocabulary.XMLSchemaNamespace });
			else
				start("grammar", new String[]{
					"xmlns",RELAXNGReader.RELAXNGNamespace,
					"datatypeLibrary", XSDVocabulary.XMLSchemaNamespace });
			
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
		return (String)exp.visit( new ExpressionVisitor(){
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
			public Object onInterleave( InterleaveExp exp ) {
				return onBinExp(exp);
			}
			public Object onConcur( ConcurExp exp ) {
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
			public Object onOther( OtherExp exp ) {
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
			public Object onList( ListExp exp ) {
				return null;
			}
			public Object onKey( KeyExp exp ) {
				return null;
			}
		});
	}
	
	
	/**
	 * namespace URI currently implied through "ns" attribute propagation.
	 */
	protected String defaultNs;
	
	
	protected DocumentHandler handler;
	/** this DocumentHandler will receive XML representation of TREX pattern. */
	public void setDocumentHandler( DocumentHandler handler ) {
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
		
		// create attributes.
		AttributeListImpl as = new AttributeListImpl();
		for( int i=0; i<attributes.length; i+=2 )
			as.addAttribute( attributes[i], "", attributes[i+1] );
		
		try {
			handler.startElement( name, as );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
	protected void end( String name ) {
		try {
			handler.endElement( name );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}
/*
	protected String[] resolveAttrQName( String name ) {
		return resolveQName( name, "" );
	}
	protected String[] resolveElementQName( String name ) {
		return resolveQName( name, TREXGrammarReader.TREXNamespace );
	}
	protected String[] resolveQName( String qname, String defaultNs ) {
		int idx = qname.indexOf(':');
		if(idx<0)
			return new String[]{defaultNs,qname,qname};
		
		String prefix = qname.substring(0,idx);
		String local = qname.substring(idx+1);
		
		if( prefix.equals("xsd") )
			return new String[]{"http://www.w3.org/2001/XMLSchema",local,qname};
		if( prefix.equals("trex") )
			return new String[]{TREXGrammarReader.TREXNamespace,local,qname};
		
		throw new Error();	// unsupported prefix name.
	}
*/	
	
	protected void characters( String str ) {
		try {
			handler.characters( str.toCharArray(), 0, str.length() );
		} catch( SAXException e ) {
			throw new SAXWrapper(e);
		}
	}

	
	private void writeNameClass( NameClass src ) {
		final String MAGIC = PossibleNamesCollector.MAGIC;
		Set names = PossibleNamesCollector.calc(src);
		
		// convert a name class to the canonical form.
		StringPair[] values = (StringPair[])names.toArray(new StringPair[names.size()]);

		Set uriset = new HashSet();
		for( int i=0; i<values.length; i++ )
			uriset.add( values[i].namespaceURI );
		
		NameClass r = null;
		String[] uris = (String[])uriset.toArray(new String[uriset.size()]);
		for( int i=0; i<uris.length; i++ ) {
			if( uris[i]==MAGIC )	continue;
			
			NameClass tmp = null;
			
			for( int j=0; j<values.length; j++ ) {
				if( !values[j].namespaceURI.equals(uris[i]) )	continue;
				if( values[j].localName==MAGIC )				continue;
				
				if( src.accepts(values[j])!=src.accepts(uris[i],MAGIC) ) {
					if(tmp==null)	tmp = new SimpleNameClass(values[j]);
					else			tmp = new ChoiceNameClass( tmp, new SimpleNameClass(values[j]) );
				}
			}
			
			if( src.accepts(uris[i],MAGIC)!=src.accepts(MAGIC,MAGIC) ) {
				if(tmp==null)
					tmp = new NamespaceNameClass(uris[i]);
				else
					tmp = new DifferenceNameClass( new NamespaceNameClass(uris[i]), tmp );
			}
			
			if(r==null)		r = tmp;
			else			r = new ChoiceNameClass(r,tmp);
		}
		
		if( src.accepts(MAGIC,MAGIC) ) {
			if( r==null )
				r = AnyNameClass.theInstance;
			else
				r = new DifferenceNameClass( AnyNameClass.theInstance, r );
		} else {
			if(r==null) {
				// this name class accepts nothing.
				// by adding notAllowed to the content model, this element
				// will match nothing.
				element("anyName");
				element("notAllowed");
				return;
			}
		}
		
		r.visit(nameClassWriter);
		
	}
	
	protected NameClassVisitor nameClassWriter = createNameClassWriter();
	protected NameClassVisitor createNameClassWriter() {
		return new NameClassWriter();
	}
	protected PatternWriter patternWriter = createPatternWriter();
	protected PatternWriter createPatternWriter() {
		return new PatternWriter();
	}
	
	
	
	/**
	 * visits NameClass and writes its XML representation.
	 * 
	 * this class can only handle canonicalized name class.
	 */
	protected class NameClassWriter implements NameClassVisitor {
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
			// should not be called.
			throw new Error();
		}
		
		public Object onChoice( ChoiceNameClass nc ) {
			start("choice");
			processChoice(nc);
			end("choice");
			return null;
		}
			
		private void processChoice( ChoiceNameClass nc ) {
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
		}
		
		public Object onDifference( DifferenceNameClass nc ) {
			if( nc.nc1 instanceof AnyNameClass ) {
				start("anyName");
				start("except");
				if( nc.nc2 instanceof ChoiceNameClass )
					processChoice( (ChoiceNameClass)nc.nc2 );
				else
					nc.nc2.visit(this);
				end("except");
				end("anyName");
			}
			else
			if( nc.nc1 instanceof NamespaceNameClass ) {
				startWithNs("nsName", ((NamespaceNameClass)nc.nc1).namespaceURI );
				start("except");
				if( nc.nc2 instanceof ChoiceNameClass )
					processChoice( (ChoiceNameClass)nc.nc2 );
				else
					nc.nc2.visit(this);
				end("except");
				end("nsName");
			}
			else
				throw new Error();
			
			return null;
		}
	}
	
	
	/** visits Expression and writes its XML representation. */
	protected class PatternWriter implements ExpressionVisitorVoid {
		
		public void onOther( OtherExp exp ) {
			exp.exp.visit(this);
		}
			
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
				writeNameClass(exp.getNameClass());
			}
			visitUnary(simplify(exp.contentModel));
			end("element");
		}
		
		/**
		 * remove unnecessary ReferenceExp from content model.
		 * this will sometimes makes content model smaller.
		 */
		public Expression simplify( Expression exp ) {
			return exp.visit( new ExpressionCloner(grammar.getPool()){
				public Expression onRef( ReferenceExp exp ) {
					if( exp2name.containsKey(exp) )
						// this ReferenceExp will be written as a named pattern.
						return exp;
					else
						// bind contents
						return exp.exp.visit(this);
				}
				public Expression onOther( OtherExp exp ) {
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
			element("text");
		}
	
		public void onInterleave( InterleaveExp exp ) {
			visitBinExp("interleave", exp, InterleaveExp.class );
		}
	
		public void onConcur( ConcurExp exp ) {
			throw new IllegalArgumentException("the grammar includes concur, which is not supported");
		}
	
		public void onList( ListExp exp ) {
			start("list");
			visitUnary(exp.exp);
			end("list");
		}

		public void onKey( KeyExp exp ) {
			String tagName;
			if( exp.isKey )	tagName = "key";
			else			tagName = "keyref";
			
			if( exp.name.namespaceURI.equals(defaultNs) )
				start(tagName,new String[]{"name",exp.name.localName});
			else
				start(tagName, new String[]{"name",exp.name.localName,"ns",exp.name.namespaceURI});
			
			// since choce,data, and value are the only possible children,
			// it is safe to rewrite the ns attribute here.
			
			exp.exp.visit(this);
			end(tagName);
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
			Expression[] children = exp.getChildren();
			for( int i=0; i<children.length; i++ )
				children[i].visit(this);
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
				writeNameClass(exp.nameClass);
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
			// This method tries to exploit this property to
			// simplify the result.
			if( exp instanceof SequenceExp ) {
				SequenceExp seq = (SequenceExp)exp;
				seq.exp1.visit(this);
				visitUnary(seq.exp2);
			}
			else
				exp.visit(this);
		}
		
		public void onTypedString( TypedStringExp exp ) {
			Datatype dt = exp.dt;
			if( dt instanceof TypedString ) {
				TypedString ts = (TypedString)dt;
				if( ts.preserveWhiteSpace )
					start("value",new String[]{"type","string"});
				else
					start("value");
					
				characters( ts.value );
					
				end("value");
				return;
			}
			
			if( dt instanceof ValueType ) {
				ValueType vt = (ValueType)dt;
				
				if( vt.baseType instanceof XSDatatypeImpl ) {
					XSDatatypeImpl base = (XSDatatypeImpl)vt.baseType;
					
					final Vector ns = new Vector();
						
					String lex = base.convertToLexicalValue( vt.value, 
					new SerializationContext() {
						public String getNamespacePrefix( String namespaceURI ) {
							int cnt = ns.size()/2;
								ns.add( "xmlns:ns"+cnt );
								ns.add( namespaceURI );
							return "ns"+cnt;
						}
					});
					
					if( base!=TokenType.theInstance ) {
						// if the type is token, we don't need @type.
						ns.add("type");
						ns.add(base.getName());
					}
					
					start("value",(String[])ns.toArray(new String[0]));
					characters(lex);
					end("value");
					return;
				}
			}
			
			if( dt instanceof XSDatatypeImpl ) {
				XSDatatypeImpl dti = (XSDatatypeImpl)dt;
					
				if( isPredefinedType(dt) ) {
					// it's a pre-defined types.
					element( "data", new String[]{"type",dti.getName()} );
				} else {
					serializeDataType(dti);
				}
				return;
			}
			
			throw new UnsupportedOperationException( dt.getClass().getName() );
		}
		
		
		/**
		 * serializes the given datatype.
		 * 
		 * The caller should generate events for &lt;simpleType&gt; element
		 * if necessary.
		 */
		protected void serializeDataType( XSDatatype dt ) {
			
			if( dt instanceof UnionType ) {
				serializeUnionType((UnionType)dt);
				return;
			}
			
			
			// store names of the applied facets into this set
			Set appliedFacets = new HashSet();
			
			// store effective facets (those which are not shadowed by another facet).
			Vector effectiveFacets = new Vector();
			
			XSDatatype x = dt;
			while( x instanceof DataTypeWithFacet || x instanceof FinalComponent ) {
				
				if( x instanceof FinalComponent ) {
					// skip FinalComponent
					x = ((FinalComponent)x).baseType;
					continue;
				}
				
				String facetName = ((DataTypeWithFacet)x).facetName;
				
				if( facetName.equals(XSDatatypeImpl.FACET_ENUMERATION) ) {
					// if it contains enumeration, then we will serialize this
					// by using <value>s.
					serializeEnumeration( (XSDatatypeImpl)dt, (EnumerationFacet)x );
					return;
				}
				
				if( facetName.equals(XSDatatypeImpl.FACET_WHITESPACE) ) {
					throw new UnsupportedOperationException("whiteSpace facet is not supported");
//					throw new Error("ws"); // ((WhiteSpaceFacet)x).whiteSpace);
				}
				
				// find the same facet twice.
				// pattern is allowed more than once.
				if( !appliedFacets.contains(facetName)
				||  appliedFacets.equals(XSDatatypeImpl.FACET_PATTERN) ) {
				
					appliedFacets.add(facetName);
					effectiveFacets.add(x);
				}
				
				x = ((DataTypeWithFacet)x).baseType;
			}

			if( x instanceof ListType ) {
				// the base type is list.
				serializeListType((XSDatatypeImpl)dt);
				return;
			}
			
			// it cannot be the union type. Union type cannot be derived by
			// restriction.
			
			// so this must be one of the pre-defined types.
			if(!(x instanceof ConcreteType ))	throw new Error(x.getClass().getName());
			
			if( x instanceof com.sun.msv.grammar.relax.EmptyStringType ) {
				// empty token will do.
				start("value");
				end("value");
				return;
			}
			if( x instanceof com.sun.msv.grammar.relax.NoneType ) {
				// "none" is equal to <notAllowed/>
				element("notAllowed");
				return;
			}
			
			// attributes to be added to this <data> element.
			Vector dataAtts = new Vector();
			
			String enclosing = null;
			
			// ID,IDREF are treated in a special manner.
			if( x instanceof com.sun.msv.grammar.IDType ) {
				enclosing = "key";
				start("key",new String[]{"ns","","name","XML_ID"});
				start("data",new String[]{"type","NCName"});
			}
			else
			if( x instanceof com.sun.msv.grammar.IDREFType ) {
				enclosing = "keyref";
				start("keyref",new String[]{"ns","","name","XML_ID"});
				start("data",new String[]{"type","NCName"});
			}
			else
				start("data",new String[]{"type",x.getName()});
			
			
			// serialize effective facets
			for( int i=effectiveFacets.size()-1; i>=0; i-- ) {
				DataTypeWithFacet dtf = (DataTypeWithFacet)effectiveFacets.get(i);
				
				if( dtf instanceof LengthFacet ) {
					param("length",
						Long.toString(((LengthFacet)dtf).length));
				} else
				if( dtf instanceof MinLengthFacet ) {
					param("minLength",
						Long.toString(((MinLengthFacet)dtf).minLength));
				} else
				if( dtf instanceof MaxLengthFacet ) {
					param("maxLength",
						Long.toString(((MaxLengthFacet)dtf).maxLength));
				} else
				if( dtf instanceof PatternFacet ) {
					String pattern = "";
					PatternFacet pf = (PatternFacet)dtf;
					for( int j=0; j<pf.exps.length; j++ ) {
						if( pattern.length()!=0 )	pattern += "|";
						pattern += pf.patterns[j];
					}
					param("pattern",pattern);
				} else
				if( dtf instanceof TotalDigitsFacet ) {
					param("totalDigits",
						Long.toString(((TotalDigitsFacet)dtf).precision));
				} else
				if( dtf instanceof FractionDigitsFacet ) {
					param("fractionDigits",
						Long.toString(((FractionDigitsFacet)dtf).scale));
				} else
				if( dtf instanceof RangeFacet ) {
					param(dtf.facetName,
						dtf.convertToLexicalValue(
							((RangeFacet)dtf).limitValue, null ));
					// we don't need to pass SerializationContext because it is only
					// for QName.
				} else
				if( dtf instanceof WhiteSpaceFacet ) {
					;	// do nothing.
				} else
					// undefined facet type
					throw new Error();
			}
			
			end("data");
			if(enclosing!=null)	end(enclosing);
		}

		protected void param( String name, String value ) {
			start("param",new String[]{"name",name});
			characters(value);
			end("param");
		}
		
		/**
		 * returns true if the specified type is a pre-defined XSD type
		 * without any facet.
		 */
		protected boolean isPredefinedType( Datatype x ) {
			return !(x instanceof DataTypeWithFacet
				|| x instanceof UnionType
				|| x instanceof ListType
				|| x instanceof FinalComponent
				|| x instanceof com.sun.msv.grammar.relax.EmptyStringType
				|| x instanceof com.sun.msv.grammar.IDType
				|| x instanceof com.sun.msv.grammar.IDREFType
				|| x instanceof com.sun.msv.grammar.relax.NoneType);
		}
		
		/**
		 * serializes a union type.
		 * this method is called by serializeDataType method.
		 */
		protected void serializeUnionType( UnionType dt ) {
			start("choice");
			
			// serialize member types.
			for( int i=0; i<dt.memberTypes.length; i++ )
				serializeDataType(dt.memberTypes[i]);
			
			end("choice");
		}
		
		/**
		 * serializes a list type.
		 * this method is called by serializeDataType method.
		 */
		protected void serializeListType( XSDatatypeImpl dt ) {
			
			ListType base = (ListType)dt.getConcreteType();
			
			if( dt.getFacetObject(dt.FACET_LENGTH)!=null ) {
				// with the length facet.
				int len = ((LengthFacet)dt.getFacetObject(dt.FACET_LENGTH)).length;
				start("list");
				for( int i=0; i<len; i++ )
					serializeDataType(base.itemType);
				end("list");
				
				return;
			}
			
			if( dt.getFacetObject(dt.FACET_MAXLENGTH)!=null )
				throw new UnsupportedOperationException("warning: maxLength facet to list type is not properly converted.");

			MinLengthFacet minLength = (MinLengthFacet)dt.getFacetObject(dt.FACET_MINLENGTH);
			
			start("list");
			if( minLength!=null ) {
				// list n times
				for( int i=0; i<minLength.minLength; i++ )
					serializeDataType(base.itemType);
			}
			start("zeroOrMore");
			serializeDataType(base.itemType);
			end("zeroOrMore");
			end("list");
		}

		/**
		 * serializes a type with enumeration.
		 * this method is called by serializeDataType method.
		 */
		protected void serializeEnumeration( XSDatatypeImpl dt, EnumerationFacet enums ) {
			
			Object[] values = enums.values.toArray();
			
			if( values.length>1 )
				start("choice");
			
			for( int i=0; i<values.length; i++ ) {
				final Vector ns = new Vector();
						
				String lex = dt.convertToLexicalValue( values[i],
					new SerializationContext() {
						public String getNamespacePrefix( String namespaceURI ) {
							int cnt = ns.size()/2;
								ns.add( "xmlns:ns"+cnt );
								ns.add( namespaceURI );
							return "ns"+cnt;
						}
					} );
				
				// make sure that the converted lexical value is allowed by this type.
				// sometimes, facets that are added later rejects some of
				// enumeration values.
				
				boolean allowed = dt.isValid( lex, 
					new ValidationContext(){
						
						public String resolveNamespacePrefix( String prefix ) {
							if( !prefix.startsWith("ns") )	return null;
							int i = Integer.parseInt(prefix.substring(2));
							return (String)ns.get(i*2+1);
						}
						
						public boolean isUnparsedEntity( String name ) {
							return true;
						}
						public boolean isNotation( String name ) {
							return true;
						}
					});
				
				ns.add("type");
				ns.add(dt.getConcreteType().getName() );
				
				if( allowed ) {
					start("value", (String[])ns.toArray(new String[0]) );
					characters(lex);
					end("value");
				}
			}

			if( values.length>1 )
				end("choice");
		}
	}
}
