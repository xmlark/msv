/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.util;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.datatype.DataType;
import com.sun.msv.reader.trex.TREXGrammarReader;
import com.sun.msv.reader.datatype.xsd.XSDVocabulary;
import com.sun.msv.datatype.DataTypeImpl;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Stack;

/**
 * generates XML representation of TREX pattern through SAX2 events.
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
	
	
	public void write( TREXGrammar grammar ) throws SAXException {
		// find a namespace URI that can be used as default "ns" attribute.
		write( grammar, (String)sniffDefaultNs(grammar.start) );
	}
	
	/**
	 * generates SAX2 events of the specified grammar.
	 * 
	 * @param defaultNs
	 *		if specified, this namespace URI is used as "ns" attribute
	 *		of grammar element. Can be null.
	 */
	public void write( TREXGrammar grammar, String defaultNs ) throws SAXException {
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
				grammar.start.visit(patternWriter);
				end("start");
			}
			
			// write all named patterns.
			ReferenceExp[] named = grammar.namedPatterns.getAll();
			for( int i=0; i<named.length; i++ ) {
				start("define",new String[]{"name",named[i].name});
				named[i].exp.visit(patternWriter);
				end("define");
			}
			end("grammar");
			handler.endDocument();
		} catch( SAXWrapper sw ) {
			throw sw.e;
		}
	}
	
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
	protected TREXPatternVisitorVoid patternWriter = createPatternWriter();
	protected TREXPatternVisitorVoid createPatternWriter() {
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
			element("ref", new String[]{"name",exp.name});
		}
	
		public void onEpsilon() {
			element("empty");
		}
	
		public void onNullSet() {
			element("nullSet");
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
			exp.visit(this);
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
			exp.exp.visit(this);
			end("mixed");
		}
	
		public void onOneOrMore( OneOrMoreExp exp ) {
			start("oneOrMore");
			exp.exp.visit(this);
			end("oneOrMore");
		}
	
		protected void onZeroOrMore( OneOrMoreExp exp ) {
			// note that this method is not a member of TREXPatternVisitor.
			start("zeroOrMore");
			exp.exp.visit(this);
			end("zeroOrMore");
		}
	
		public void onElement( ElementExp exp ) {
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
			visitUnary(exp.contentModel);
			end("element");
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
				exp.exp.visit(this);
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
				}
			
				throw new UnsupportedOperationException();
			} catch( SAXException e ) {
				throw new SAXWrapper(e);
			}
		}
	}
}
