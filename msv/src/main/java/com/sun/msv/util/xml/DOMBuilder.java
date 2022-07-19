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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * builds DOM from SAX2 event stream.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DOMBuilder extends DefaultHandler {
    
    private final Document dom;
    private Node parent;
    
    public DOMBuilder( Document document ) {
        this.dom = document;
        parent = dom;
    }
    
    public DOMBuilder() throws ParserConfigurationException {
        this( DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() );
    }
    
    /**
     * returns DOM. This method should be called after the parsing was completed.
     */
    public Document getDocument() {
        return dom;
    }
    
    public void startElement( String ns, String local, String qname, Attributes atts ) {
        Element e = dom.createElementNS( ns, qname );
        parent.appendChild(e);
        parent = e;
        
        for( int i=0; i<atts.getLength(); i++ )
            e.setAttributeNS( atts.getURI(i), atts.getQName(i), atts.getValue(i) );
    }
    
    public void endElement( String ns, String local, String qname ) {
        parent = parent.getParentNode();
    }
    
    public void characters( char[] buf, int start, int len ) {
        parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
    }
    
    public void ignorableWhitespace( char[] buf, int start, int len ) {
        parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
    }
}
