package com.sun.msv.schematron;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.schematron.reader.SRELAXNGReader;
import com.sun.msv.schematron.verifier.RelmesVerifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

public class Driver
{
	public static void main( String args[] ) throws Exception {
		System.out.println("relmes verifier   Copyright(C) Sun Microsystems, Inc. 2001");
			
		if(args.length<2) {
			System.out.println(
				"Usage: relames <schema file> <document1> [<document2> ...]");
			return;
		}
		
		
		System.out.println("parsing    "+args[0]);
		// parse a grammar
		SAXParserFactory factory = javax.xml.parsers.SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		Grammar grammar = SRELAXNGReader.parse( args[0], factory, new DebugController(false,false) );
		if(grammar==null)	return;
		
		// setup verifier
		RelmesVerifier verifier = new RelmesVerifier(
			new REDocumentDeclaration(grammar), new ReportErrorHandler() );
		XMLReader reader = factory.newSAXParser().getXMLReader();
		reader.setContentHandler(verifier);
		
		try {
		for( int i=1; i<args.length; i++ ) {
			System.out.println("validating "+args[i]);
			reader.parse(args[i]);
			if(verifier.isValid())
				System.out.println("valid\n");
			else
				System.out.println("NOT valid\n");
		}
		} catch( org.xml.sax.SAXException e ) {
			if(e.getException()!=null)
				e.getException().printStackTrace();
			throw e;
		}
	}
}
