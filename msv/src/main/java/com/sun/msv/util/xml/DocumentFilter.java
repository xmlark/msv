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

package com.sun.msv.util.xml;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/**
 * SAX DocumentHandler event interceptor.
 * 
 * This object acts as a filter to DocumentHandler events.
 * Derived class should override methods of interest and
 * perform somethings.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DocumentFilter implements DocumentHandler
{
    public DocumentHandler next;
    
    public DocumentFilter( DocumentHandler next ) {
        this.next = next;
    }
    
    public void startDocument() throws SAXException {
        next.startDocument();
    }
    public void endDocument() throws SAXException {
        next.endDocument();
    }
    public void startElement( String name, AttributeList atts ) throws SAXException {
        next.startElement(name,atts);
    }
    public void endElement( String name ) throws SAXException {
        next.endElement(name);
    }
    public void characters( char[] buf, int start, int len ) throws SAXException {
        next.characters(buf,start,len);
    }
    public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
        next.ignorableWhitespace(buf,start,len);
    }
    public void processingInstruction( String target, String data ) throws SAXException {
        next.processingInstruction(target,data);
    }
    public void setDocumentLocator( Locator loc ) {
        next.setDocumentLocator(loc);
    }
}
