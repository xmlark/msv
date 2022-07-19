/*
 * Copyright (c) 1998-2013 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.msv.scanner.dtd;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * do-nothing implementation of DTDEventHandler.
 */
public class DTDHandlerBase implements DTDEventListener {

    public void processingInstruction(String target, String data) 
                throws SAXException {
    }
    
    public void setDocumentLocator( Locator loc ) {
    }

    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    public void warning(SAXParseException err) throws SAXException {
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    public void unparsedEntityDecl(String name, String publicId, 
                                   String systemId, String notationName) throws SAXException {
    }

    public void endDTD() throws SAXException {
    }

    public void externalGeneralEntityDecl(String n, String p, String s)  throws SAXException {
    }

    public void internalGeneralEntityDecl (String n, String v) throws SAXException {
    }

    public void externalParameterEntityDecl (String n, String p, String s) throws SAXException {
    }

    public void internalParameterEntityDecl (String n, String v) throws SAXException {
    }

    public void startDTD (InputEntity in) throws SAXException {
    }

    public void comment (String n) throws SAXException {
    }

    public void characters (char ch[], int start, int length) throws SAXException {
    }

    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
    }

    public void startCDATA () throws SAXException {
    }

    public void endCDATA () throws SAXException {
    }
    
    
    public void startContentModel( String elementName, short contentModelType ) throws SAXException {
    }
    public void endContentModel( String elementName, short contentModelType ) throws SAXException {
    }

    public void attributeDecl(
        String elementName, String attributeName, String attributeType,
        String[] enumeration, short attributeUse, String defaultValue ) throws SAXException {
    }
    
    public void childElement( String elementName, short occurence ) throws SAXException {}
    public void mixedElement( String elementName ) throws SAXException {}
    public void startModelGroup() throws SAXException {}
    public void endModelGroup( short occurence ) throws SAXException {}
    public void connector( short connectorType ) throws SAXException {}
}

