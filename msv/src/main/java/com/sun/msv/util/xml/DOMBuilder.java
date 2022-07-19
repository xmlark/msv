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
