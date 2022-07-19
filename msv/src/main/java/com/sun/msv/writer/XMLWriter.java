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

package com.sun.msv.writer;

import org.xml.sax.DocumentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributeListImpl;

/**
 * Helper class that wraps {@link DocumentHandler} and provides utility methods.
 * 
 * <p>
 * Note that this class uses DocumentHandler, not ContentHandler.
 * This generally allows the caller better control.
 * 
 * <p>
 * This class throws {@link SAXRuntimeException}, instead of SAXException.
 */
public class XMLWriter
{
    protected DocumentHandler handler;
    /** this DocumentHandler will receive XML. */
    public void setDocumentHandler( DocumentHandler handler ) {
        this.handler = handler;
    }
    public DocumentHandler getDocumentHandler() { return handler; }
    
    public void element( String name ) {
        element( name, new String[0] );
    }
    public void element( String name, String[] attributes ) {
        start(name,attributes);
        end(name);
    }
    public void start( String name ) {
        start(name, new String[0] );
    }
    public void start( String name, String[] attributes ) {
        
        // create attributes.
        AttributeListImpl as = new AttributeListImpl();
        for( int i=0; i<attributes.length; i+=2 )
            as.addAttribute( attributes[i], "", attributes[i+1] );
        
        try {
            handler.startElement( name, as );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
    public void end( String name ) {
        try {
            handler.endElement( name );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
    
    public void characters( String str ) {
        try {
            handler.characters( str.toCharArray(), 0, str.length() );
        } catch( SAXException e ) {
            throw new SAXRuntimeException(e);
        }
    }
}
