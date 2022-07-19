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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.Verifier;
import org.iso_relax.verifier.VerifierConfigurationException;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.verifier.IVerifier;

/**
 * base implementation of RELAXFactoryImpl and TREXFactoryImpl
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SchemaImpl implements Schema
{
    protected final Grammar grammar;
    protected final SAXParserFactory factory;
    
    protected SchemaImpl( Grammar grammar, SAXParserFactory factory,
        boolean _usePanicMode ) {
        
        this.grammar = grammar;
        this.factory = factory;
        this.usePanicMode = _usePanicMode;
    }
    
    public SchemaImpl( Grammar grammar ) {
        this.grammar = grammar;
        this.factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        this.usePanicMode = false;
    }
    
    public Verifier newVerifier() throws VerifierConfigurationException {
        IVerifier core = FactoryImpl.createVerifier(grammar);
        core.setPanicMode(usePanicMode);
        return new VerifierImpl( core, createXMLReader() );
    }
    
    private synchronized XMLReader createXMLReader() throws VerifierConfigurationException {
        // SAXParserFactory is not thread-safe. Thus we need to
        // synchronize this method.
        try {
            return factory.newSAXParser().getXMLReader();
        } catch( SAXException e ) {
            throw new VerifierConfigurationException(e);
        } catch( ParserConfigurationException e ) {
            throw new VerifierConfigurationException(e);
        }
    }
    
    private boolean usePanicMode;
}
