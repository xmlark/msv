/*
 * @(#)$Id$
 *
 * Copyright 2002 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schematron.jarv;

import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.schematron.reader.SRELAXNGReader;

/**
 * {@link org.iso_relax.verifier.VerifierFactory} implementation
 * for RELAX NG + schematron.
 * 
 * @author
 * 	Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class RelamesFactoryImpl extends VerifierFactory {
    
    private final SAXParserFactory factory;
    
    /**
     * Constructor for RelamesFactoryImpl.
     * @param factory
     *      This parser factory will be used to read RELAX NG schemas.
     */
    public RelamesFactoryImpl(SAXParserFactory _factory) {
        this.factory = _factory;
        factory.setNamespaceAware(true);    // people often forget this
    }

    /**
     * Constructor for RelamesFactoryImpl.
     * Use the default {@link SAXParserFactory} to parse schemas.
     */
    public RelamesFactoryImpl() {
        this.factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
    }

    public Schema compileSchema(InputSource source)
        throws VerifierConfigurationException, SAXException, IOException {
        
        try {
            Grammar grammar = SRELAXNGReader.parse( source, factory, new ThrowController() );
            if(grammar==null)
                throw new VerifierConfigurationException("unable to parse schema:"+source.getSystemId());
            return new RelamesSchemaImpl(grammar);
        } catch( WrapperException e ) {
            throw e.e;  // re-throw
        }
        
    }

    // TODO: merge this with FactoryImpl

    /** wrapper exception so that we can throw it from the GrammarReaderController. */
    private static class WrapperException extends RuntimeException {
        WrapperException( SAXException e ) {
            super(e.getMessage());
            this.e = e;
        }
        public final SAXException e;
    }
    private class ThrowController implements GrammarReaderController {
        public void warning( Locator[] locs, String errorMessage ) {}
        public void error( Locator[] locs, String errorMessage, Exception nestedException ) {
            for( int i=0; i<locs.length; i++ )
                if(locs[i]!=null)
                    throw new WrapperException(
                        new SAXParseException(errorMessage,locs[i],nestedException));
            
            throw new WrapperException(
                new SAXException(errorMessage,nestedException));
        }
        public InputSource resolveEntity( String p, String s ) throws SAXException, IOException {
            EntityResolver er = RelamesFactoryImpl.this.getEntityResolver();
            if(er==null)      return null;
            else              return er.resolveEntity(p,s);
        }
    }
}
