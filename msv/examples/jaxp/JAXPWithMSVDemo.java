/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package jaxp;

import java.io.File;
import javax.xml.parsers.*;
import com.sun.msv.verifier.jaxp.SAXParserFactoryImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;

/**
 * Uses JAXP implementation of MSV to plug validation capability
 * into the existing application.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class JAXPWithMSVDemo
{
    public static void main( String[] args ) throws Exception {
        
        if( args.length<2 ) {
            System.out.println("JAXPWithMSVDemo <schema file> <instance files> ...");
            return;
        }

        // create SAXParserFactory that performs validation by the specified schema
        // this method will throw an exception if it fails to parse the document.
        SAXParserFactory factory = new SAXParserFactoryImpl(new File(args[0]));
        
        
        // once the parser factory is created, just do as you always do.
        factory.setNamespaceAware(true);
        SAXParser parser = factory.newSAXParser();
        
        for( int i=1; i<args.length; i++ ) {
            // validation errors will be reported just like any other errors.
            final String fileName = args[i];
            parser.parse( new File(fileName), new DefaultHandler() {
                    
                boolean isValid = true;
                    
                public void error( SAXParseException e ) throws SAXException {
                    System.out.println( e );
                    isValid = false;
                }
                public void fatalError( SAXParseException e ) throws SAXException {
                    System.out.println( e );
                    isValid = false;
                }
                public void endDocument() {
                    if(isValid)
                        // successfully parsed without any error.
                        System.out.println(fileName+" is valid");
                    else
                        System.out.println(fileName+" is NOT valid");
                }
            });
        }
    }
}
