/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
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
