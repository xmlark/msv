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

package com.sun.msv.reader.util;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * feeds SAX events to two ContentHandlers.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ForkContentHandler implements ContentHandler
{
    protected ContentHandler lhs;
    protected ContentHandler rhs;
    
    public ForkContentHandler( ContentHandler lhs, ContentHandler rhs ) {
        this.lhs = lhs;
        this.rhs = rhs;
    }
    
    public void setDocumentLocator (Locator locator) {
        lhs.setDocumentLocator(locator);
        rhs.setDocumentLocator(locator);
    }
    
    public void startDocument() throws SAXException {
        lhs.startDocument();
        rhs.startDocument();
    }
        
    public void endDocument () throws SAXException {
        lhs.endDocument();
        rhs.endDocument();
    }

    public void startPrefixMapping (String prefix, String uri) throws SAXException {
        lhs.startPrefixMapping(prefix,uri);
        rhs.startPrefixMapping(prefix,uri);
    }

    public void endPrefixMapping (String prefix) throws SAXException {
        lhs.endPrefixMapping(prefix);
        rhs.endPrefixMapping(prefix);
    }
        
    public void startElement (String uri, String localName, String qName, Attributes attributes) throws SAXException {
        lhs.startElement(uri,localName,qName,attributes);
        rhs.startElement(uri,localName,qName,attributes);
    }
        
    public void endElement (String uri, String localName, String qName) throws SAXException {
        lhs.endElement(uri,localName,qName);
        rhs.endElement(uri,localName,qName);
    }
        
    public void characters (char ch[], int start, int length) throws SAXException {
        lhs.characters(ch,start,length);
        rhs.characters(ch,start,length);
    }
        
    public void ignorableWhitespace (char ch[], int start, int length) throws SAXException {
        lhs.ignorableWhitespace(ch,start,length);
        rhs.ignorableWhitespace(ch,start,length);
    }
        
    public void processingInstruction (String target, String data) throws SAXException {
        lhs.processingInstruction(target,data);
        rhs.processingInstruction(target,data);
    }

    public void skippedEntity (String name) throws SAXException {
        lhs.skippedEntity(name);
        rhs.skippedEntity(name);
    }
}
