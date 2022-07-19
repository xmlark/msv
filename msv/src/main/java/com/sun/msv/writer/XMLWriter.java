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
