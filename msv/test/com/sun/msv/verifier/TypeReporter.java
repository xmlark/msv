/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.trex.typed.TypedElementPattern;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.util.ExpressionPrinter;
import com.sun.msv.reader.trex.typed.TypedTREXGrammarInterceptor;
import com.sun.msv.reader.trex.classic.TREXGrammarReader;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.VerifierFilter;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.relaxng.datatype.Datatype;

/**
 * dumps RELAX label assigned to each element.
 * 
 * Example of type-assignment.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeReporter extends DefaultHandler
{
	
	public static void main( String[] args ) throws Exception {
		new TypeReporter().run(args);
	}
	
	private VerifierFilter filter;
	
	private void run( String[] args ) throws Exception {
		if( args.length!=3 ) {
			System.out.println("Usage: TypeReporter (relaxNS|relaxCore|trex|xsd) <schema> <XML instance>\n");
			return;
		}
		
//		SAXParserFactory factory = new org.apache.crimson.jaxp.SAXParserFactoryImpl();
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		Grammar grammar;
		
		if( args[0].equals("trex") ) {
			TREXGrammarReader reader = new TREXGrammarReader(
				new com.sun.msv.driver.textui.DebugController(false,false),
				factory,
				new TypedTREXGrammarInterceptor(),
				new ExpressionPool() );
			((XMLFilter)reader).parse(args[1]);
			grammar = reader.getResult();
		} else {
			grammar = GrammarLoader.loadSchema( args[1],
				new com.sun.msv.driver.textui.DebugController(false,false),
				factory );
		}
		
		if( grammar==null ) {
			System.err.println("failed to load a grammar");
			return;
		}
		
		filter = new VerifierFilter( new REDocumentDeclaration(grammar),
			new com.sun.msv.driver.textui.ReportErrorHandler() );
		
		filter.setParent(factory.newSAXParser().getXMLReader());
		filter.setContentHandler(this);
		filter.parse( args[2] );
	}
	
	
	private int indent = 0;
	
	private void printIndent() {
		for( int i=0; i<indent; i++ )
			System.out.print("  ");
	}
	
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts ) {
		printIndent();
		indent++;
		System.out.print("<"+qName+"> :");
		
		Object o = filter.getVerifier().getCurrentElementType();
		
		if( o instanceof ElementRule ) {
			// for RELAX
			ElementRule er = (ElementRule)o;
			if( er.getParent()==null )
				System.out.println("##inline");
			else
				System.out.println(er.getParent().name);
			return;
		}
		if( o instanceof TypedElementPattern ) {
			// for typed TREX
			System.out.println( ((TypedElementPattern)o).label );
			return;
		}
		if( o instanceof ElementPattern ) {
			System.out.println( ExpressionPrinter.printContentModel(
				((ElementPattern)o).contentModel ) ); 
			return;
		}
		
		System.out.println("???");
	}
	
	public void endElement( String namespaceUri, String localName, String qName ) {
		Datatype[] types = filter.getVerifier().getLastCharacterType();
		if( types!=null ) {
			String r="";
			for( int i=0; i<types.length; i++ ) {
				if( types[i] instanceof XSDatatype )
					r+=((XSDatatype)types[i]).displayName()+" ";
				else
					r+=types[i]+" ";
			}
			
			printIndent();
			System.out.println("-- "+r+" --");
		}
		indent--;
		printIndent();
		System.out.println("</"+qName+">");
	}

}
