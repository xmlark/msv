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

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.trex.TREXPatternPool;
import com.sun.msv.grammar.trex.typed.TypedElementPattern;
import com.sun.msv.reader.relax.core.RELAXCoreReader;
import com.sun.msv.relaxns.reader.RELAXNSReader;
import com.sun.msv.reader.trex.typed.TypedTREXGrammarInterceptor;
import com.sun.msv.reader.trex.TREXGrammarReader;
import com.sun.msv.verifier.VerifierFilter;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;
import org.xml.sax.XMLFilter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


/**
 * dumps RELAX label assigned to each element.
 * 
 * Example of type-assignment.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypeReporter extends DefaultHandler
{
	public static void main( String[] args ) throws Exception
	{
		new TypeReporter().run(args);
	}
	
	private VerifierFilter filter;
	
	private void run( String[] args ) throws Exception
	{
		if( args.length!=3 )
		{
			System.out.println("Usage: TypeReporter (relaxNS|relaxCore|trex) <schema> <XML instance>\n");
			return;
		}
		
//		SAXParserFactory factory = new org.apache.crimson.jaxp.SAXParserFactoryImpl();
		SAXParserFactory factory = new org.apache.xerces.jaxp.SAXParserFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		Grammar grammar;
		
		if( args[0].equals("relaxCore") )
			grammar = RELAXCoreReader.parse(
					args[1],
					factory,
					new com.sun.msv.driver.textui.DebugController(false),
					new TREXPatternPool() );
		else
		if( args[0].equals("relaxNS") )
			grammar = RELAXNSReader.parse(
					args[1],
					factory,
					new com.sun.msv.driver.textui.DebugController(false),
					new TREXPatternPool() );
		else {
			TREXGrammarReader reader = new TREXGrammarReader(
				new com.sun.msv.driver.textui.DebugController(false),
				factory,
				new TypedTREXGrammarInterceptor(),
				new TREXPatternPool() );
			((XMLFilter)reader).parse(args[1]);
			grammar = reader.getResult();
		}
		// use TREXPatternPool so that we can verify it like TREX.
		
		if( grammar==null )
		{
			System.err.println("failed to load a grammar");
			return;
		}
		
		filter = new VerifierFilter( new TREXDocumentDeclaration(grammar),
			new com.sun.msv.driver.textui.ReportErrorHandler() );
		
		filter.setParent(factory.newSAXParser().getXMLReader());
		filter.setContentHandler(this);
		filter.parse( args[2] );
	}
	
	
	private int indent = 0;
	
	private void printIndent()
	{
		for( int i=0; i<indent; i++ )
			System.out.print("  ");
	}
	
	
	public void startElement( String namespaceUri, String localName, String qName, Attributes atts )
	{
		printIndent();
		indent++;
		System.out.print("<"+qName+"> :");
		
		Object o = filter.getVerifier().getCurrentElementType();
		
		if( o instanceof ElementRule )
		{// for RELAX
			ElementRule er = (ElementRule)o;
			if( er.getParent()==null )
				System.out.println("##inline");
			else
				System.out.println(er.getParent().name);
			return;
		}
		if( o instanceof TypedElementPattern )
		{// for TREX
			System.out.println( ((TypedElementPattern)o).label );
			return;
		}
		
		System.out.println("???");
	}
	
	public void endElement( String namespaceUri, String localName, String qName )
	{
		if( filter.getVerifier().getLastCharacterType()!=null )
		{
			printIndent();
			System.out.println("-- "+filter.getVerifier().getLastCharacterType().getName()+" --");
		}
		indent--;
		printIndent();
		System.out.println("</"+qName+">");
	}

}
