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
import java.io.InputStream;

import javax.xml.parsers.SAXParser;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderAdapter;

/**
 * SAXParser implementation that supports validation.
 * 
 * <p>
 * This class uses another SAXParser implementation and 
 * adds the validation capability to it.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class SAXParserImpl extends SAXParser
{
    /** The underlying SAX parser. */
    private final SAXParser core;
    /**
     * JARV verifier object that performs the validation for this SAXParserImpl.
     * This field is null when no schema is set.
     */
    private Verifier verifier;
    /** A reference to VerifierFactory that can be used to parse a schema. */
    private final VerifierFactory factory;
    
    
    SAXParserImpl( SAXParser core, VerifierFactory _jarvFactory, Verifier _verifier ) {
        this.core = core;
        this.factory = _jarvFactory;
        this.verifier = _verifier;
    }
    
        /** @deprecated */
    public org.xml.sax.Parser getParser() throws SAXException {
        // maybe we should throw an UnsupportedOperationException,
        // rather than doing a trick like this.
        return new XMLReaderAdapter(getXMLReader());
    }
    
    public Object getProperty( String name )
        throws SAXNotRecognizedException, SAXNotSupportedException {
        
        return core.getProperty(name);
    }
    
    public void setProperty( String name, Object value )
        throws SAXNotRecognizedException, SAXNotSupportedException {
        
        if( Const.SCHEMA_PROPNAME.equals(name) ) {
            try {
                if(value instanceof String) {
                    verifier = factory.newVerifier( (String)value );
                    return;
                }
                if(value instanceof File) {
                    verifier = factory.newVerifier( (File)value );
                    return;
                }
                if(value instanceof InputSource) {
                    verifier = factory.newVerifier( (InputSource)value );
                    return;
                }
                if(value instanceof InputStream) {
                    verifier = factory.newVerifier( (InputStream)value );
                    return;
                }
                if(value instanceof Schema) {
                    verifier = ((Schema)value).newVerifier();
                    return;
                }
                throw new SAXNotSupportedException("unrecognized value type: "+value.getClass().getName() );
            } catch( Exception e ) {
                // TODO: what is the correct exception type?
                throw new SAXNotRecognizedException(e.toString());
            }
        }
        
        core.setProperty(name,value);
    }
    
    public XMLReader getXMLReader() throws SAXException {
        XMLReader reader = core.getXMLReader();
        if(verifier==null)    return reader;    // no validation is necessary.
        
        XMLFilter filter = verifier.getVerifierFilter();
        filter.setParent(reader);
        return filter;
    }
    
    public boolean isNamespaceAware() {
        return core.isNamespaceAware();
    }
    
    public boolean isValidating() {
        return core.isValidating();
    }
}
