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

package com.sun.msv.reader;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/**
 * Internal view of GrammarReaderController.
 * 
 * This class wraps a GrammarReaderController and
 * adds several convenient methods for the caller.
 */
public class Controller implements GrammarReaderController2, ErrorHandler
{
    /** Controller works as a wrapper to this object. */
    private final GrammarReaderController core;
    public GrammarReaderController getCore() { return core; }
    
    /** This flag will be set to true in case of any error. */
    private boolean _hadError = false;
    
    /** Returns true if an error had been reported. */
    public boolean hadError() { return _hadError; }
    
    /** Force set the error flag to true. */
    public final void setErrorFlag() { _hadError=true; }
        
    public Controller( GrammarReaderController _core ) {
        this.core = _core;
    }
    
    @Deprecated
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        return core.resolveEntity(publicId, systemId);
    }
    
    public void warning( Locator[] locs, String errorMessage ) {
        core.warning(locs,errorMessage);
    }
    
    public void error( Locator[] locs, String errorMessage, Exception nestedException ) {
        setErrorFlag();
        core.error(locs,errorMessage,nestedException);
    }

    public void error( String errorMessage, Exception nestedException ) {
        error( new Locator[0], errorMessage, nestedException );
    }
    
    public void fatalError( SAXParseException spe ) {
        error(spe);
    }
    
    public void error( SAXParseException spe ) {
        error( getLocator(spe), spe.getMessage(), spe.getException() );
    }
    
    public void warning( SAXParseException spe ) {
        warning( getLocator(spe), spe.getMessage() );
    }
    
    public void error( IOException e, Locator source ) {
        error( new Locator[]{source}, e.getMessage(), e );
    }
    
    public void error( SAXException e, Locator source ) {
        // if a nested exception is a RuntimeException,
        // this shouldn't be handled.
        if( e.getException() instanceof RuntimeException )
            throw (RuntimeException)e.getException();
        
        if(e instanceof SAXParseException)
            error( (SAXParseException)e );
        else
            error( new Locator[]{source}, e.getMessage(), e );
    }
    
    public void error( ParserConfigurationException e, Locator source ) {
        error( new Locator[]{source}, e.getMessage(), e );
    }
    
    
    
    protected Locator[] getLocator( SAXParseException spe ) {
        LocatorImpl loc = new LocatorImpl();
        loc.setColumnNumber( spe.getColumnNumber() );
        loc.setLineNumber( spe.getLineNumber() );
        loc.setSystemId( spe.getSystemId() );
        loc.setPublicId( spe.getPublicId() );
        
        return new Locator[]{loc};
    }

    /**
     * Return the full resolver.
     */
	public LSResourceResolver getLSResourceResolver() {
		if (core instanceof GrammarReaderController2) {
			return ((GrammarReaderController2)core).getLSResourceResolver();
		} else {
			return null;
		}
	}
}
