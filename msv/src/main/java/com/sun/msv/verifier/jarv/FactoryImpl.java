/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.verifier.jarv;

import java.io.IOException;

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.util.ErrorHandlerImpl;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class FactoryImpl extends VerifierFactory {
    protected final SAXParserFactory factory;
    
    protected FactoryImpl( SAXParserFactory factory ) {
        this.factory = factory;
    }
    protected FactoryImpl() {
        factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
    }
    
    
    private boolean usePanicMode = true;
    
    public void setFeature( String feature, boolean v )
            throws SAXNotRecognizedException,SAXNotSupportedException {
        if(feature.equals(Const.PANIC_MODE_FEATURE))
            usePanicMode = v;
        else
            super.setFeature(feature,v);
    }
    
    public boolean isFeature( String feature )
            throws SAXNotRecognizedException,SAXNotSupportedException {
        
        if(feature.equals(Const.PANIC_MODE_FEATURE))
            return usePanicMode;
        else
            return super.isFeature(feature);
    }
    
    
    /**
     * To be used to resolve files included/imported by the schema. Can be null.
     */
    private EntityResolver resolver;
    
    public void setEntityResolver( EntityResolver _resolver ) {
        this.resolver = _resolver;
    }
    public EntityResolver getEntityResolver() {
        return resolver;
    }
    
    

    /**
     * parses a Grammar from the specified source.
     * return null if an error happens.
     */
    protected abstract Grammar parse(
        InputSource source, GrammarReaderController controller )
            throws SAXException,VerifierConfigurationException;
    
    
    public Schema compileSchema( InputSource source )
        throws VerifierConfigurationException, SAXException {
        try {
            Grammar g = parse(source,new ThrowController());
            if(g==null)
                // theoretically this isn't possible because we throw an exception
                // if an error happens.
                throw new VerifierConfigurationException("unable to parse the schema");
            return new SchemaImpl(g,factory,usePanicMode);
        } catch( WrapperException we ) {
            throw we.e;
        } catch( Exception pce ) {
            throw new VerifierConfigurationException(pce);
        }
    }
    
    
    
    /**
     * gets the VGM by sniffing its type.
     * 
     * <p>
     * To validate XML Schema correctly, we need to use the specialized VGM.
     */
    static IVerifier createVerifier( Grammar g ) {
        if( g instanceof XMLSchemaGrammar )
            return new IDConstraintChecker(
                (XMLSchemaGrammar)g,
                new ErrorHandlerImpl() );
        else
            return new com.sun.msv.verifier.Verifier(
                new REDocumentDeclaration(g),
                new ErrorHandlerImpl() );
    }
    
    
    
    /** wrapper exception so that we can throw it from the GrammarReaderController. */
    @SuppressWarnings("serial")
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
            if(resolver==null)        return null;
            else                    return resolver.resolveEntity(p,s);
        }
    }
}
