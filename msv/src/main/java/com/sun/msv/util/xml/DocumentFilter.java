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
