/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package xpathloc;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.iso_relax.verifier.VerifierHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * Demonstrates how to use {@link xpathloc.XPathLocationTracker}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 * 
 * @see
 *     XPathLocationTracker
 */
public class Main {
    public static void main(String[] args) throws Exception {
        if( args.length<2 ) {
            System.out.println("Main <schema file> <instance file 1> <instance file 2> ...");
            return;
        };
        
        // see JARVDemo for more about how you "properly" use JARV.
        
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        Verifier verifier = factory.newVerifier(new File(args[0]));
        
        // create a SAX Parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        XMLReader reader = spf.newSAXParser().getXMLReader();
        
        // set up a pipeline
        VerifierHandler handler = verifier.getVerifierHandler();
        XPathLocationTracker tracker = new XPathLocationTracker(handler);
        reader.setContentHandler(tracker);

        verifier.setErrorHandler( new ErrorHandlerImpl(tracker) );
        
        for( int i=1; i<args.length; i++ ) {
            System.out.println("parsing "+args[i]);
            reader.parse(new InputSource(new FileInputStream(args[i])));
        }
    }
    
    private static class ErrorHandlerImpl implements ErrorHandler {
        
        private final XPathLocationTracker tracker;
        
        ErrorHandlerImpl(XPathLocationTracker _tracker) {
            this.tracker = _tracker;
        }
        
        public void warning(SAXParseException exception) throws SAXException {
            print("warning ",exception);
        }

        public void error(SAXParseException exception) throws SAXException {
            print("error ",exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            print("fatal ",exception);
        }

        private void print(String name, SAXParseException exception) {
            System.out.println(name + exception.getMessage());
            System.out.println("  "+tracker.getXPath());
        }
        
    }
}
