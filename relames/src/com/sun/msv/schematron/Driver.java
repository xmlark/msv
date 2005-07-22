package com.sun.msv.schematron;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.schematron.reader.SRELAXNGReader;
import com.sun.msv.schematron.verifier.RelmesVerifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParserFactory;

public class Driver
{
	public static void main( final String[] args ) throws Exception {
		System.out.println("relmes verifier   Copyright(C) Sun Microsystems, Inc. 2001");
			
		if(args.length<2) {
			System.out.println(
				"Usage: relames <schema file> <document1> [<document2> ...]");
			return;
		}

        Thread t = new Thread() {
            public void run() {
                try {
                    doMain(args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
        t.join();
    }

    public static void doMain( String[] args ) throws Exception {
		System.out.println("parsing    "+args[0]);
		// parse a grammar
		SAXParserFactory factory = SAXParserFactory.newInstance();
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
		} catch( SAXException e ) {
			if(e.getException()!=null)
				e.getException().printStackTrace();
			throw e;
		}
	}
}
