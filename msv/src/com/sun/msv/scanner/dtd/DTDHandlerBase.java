/*
 * @(#)DTDHandlerBase.java    1.6 00/04/25
 *
 * Copyright 2000 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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

