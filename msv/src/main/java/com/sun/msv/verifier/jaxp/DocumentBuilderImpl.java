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

package com.sun.msv.verifier.jaxp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * DocumentBuilder implementation that supports validation.
 * 
 * <p>
 * This class uses another DocumentBuilder implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class DocumentBuilderImpl extends DocumentBuilder
{
    /**
     * Wrapped DocumentBuilder that does everything else.
     */
    private final DocumentBuilder core;

    /**
     * The validation will be performed using this verifier.
     */
    private final Verifier verifier;
    
    DocumentBuilderImpl( DocumentBuilder _core, Schema _schema ) throws ParserConfigurationException {
        this.core = _core;
        try {
            verifier = _schema.newVerifier();
        } catch( Exception e ) {
            // this will not happen with our implementation of JARV.
            throw new ParserConfigurationException(e.toString());
        }
        // set an error handler to throw an exception in case of error.
        verifier.setErrorHandler( com.sun.msv.verifier.util.ErrorHandlerImpl.theInstance );
    }
    
    
    public DOMImplementation getDOMImplementation() {
        return core.getDOMImplementation();
    }
    
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    
    public boolean isValidating() {
        return true;
    }
    
    public Document newDocument() {
        return core.newDocument();
    }
    
    public Document parse( InputSource is ) throws SAXException, IOException {
        return verify(core.parse(is));
    }
    
    public Document parse( File f ) throws SAXException, IOException {
        return verify(core.parse(f));
    }
    
    public Document parse( InputStream is ) throws SAXException, IOException {
        return verify(core.parse(is));
    }
    
    public Document parse( InputStream is, String systemId ) throws SAXException, IOException {
        return verify(core.parse(is,systemId));
    }
    
    public Document parse( String url ) throws SAXException, IOException {
        return verify(core.parse(url));
    }
    
    public void setEntityResolver( EntityResolver resolver ) {
        verifier.setEntityResolver(resolver);
        core.setEntityResolver(resolver);
    }
    
    public void setErrorHandler( ErrorHandler handler ) {
        verifier.setErrorHandler(handler);
        core.setErrorHandler(handler);
    }
    
    
    
    
    /**
     * Validates a given DOM and returns it if it is valid. Otherwise throw an exception.
     */
    private Document verify( Document dom ) throws SAXException, IOException {
        if(verifier.verify(dom))
            return dom;    // the document is valid
        
        // this is strange because if any error happens, the error handler
        // will throw an exception.
        throw new SAXException("the document is invalid");
    }
}
