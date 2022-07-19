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

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/**
 * produces SAX2 event from a DOM tree.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SAXEventGenerator {
    
    /**
     * scans the specified DOM and sends SAX2 events to the handler.
     */
    public static void parse( Document dom, final ContentHandler handler ) throws SAXException {
        
        DOMVisitor visitor = new DOMVisitor(){
            public void visit( Element e ) {
                int attLen = e.getAttributes().getLength();
                AttributesImpl atts = new AttributesImpl();
                for( int i=0; i<attLen; i++ ) {
                    Attr a = (Attr)e.getAttributes().item(i);
                    
                    String uri = a.getNamespaceURI();
                    String local = a.getLocalName();
                    if(uri==null)    uri="";
                    if(local==null)    local=a.getName();
                    
                    atts.addAttribute( uri,local,
                        a.getName(), null/*no type available*/, a.getValue() );
                }
                
                try {
                    String uri = e.getNamespaceURI();
                    String local = e.getLocalName();
                    if(uri==null)    uri="";
                    if(local==null)    local=e.getNodeName();
                    
                    handler.startElement( uri, local, e.getNodeName(), atts );
                    super.visit(e);
                    handler.endElement( uri, local, e.getNodeName() );
                } catch( SAXException x ) {
                    throw new SAXWrapper(x);
                }
            }
            
            public void visitNode( Node n ) {
                if( n.getNodeType()==Node.TEXT_NODE
                ||  n.getNodeType()==Node.CDATA_SECTION_NODE ) {
                    String text = n.getNodeValue();
                    try {
                        handler.characters( text.toCharArray(), 0, text.length() );
                    } catch( SAXException x ) {
                        throw new SAXWrapper(x);
                    }
                }
                super.visitNode(n);
            }
        };
        
        // set a dummy locator. We cannot provide location information.
        handler.setDocumentLocator( new LocatorImpl() );
        handler.startDocument();
        try {
            visitor.visit(dom);
        } catch( SAXWrapper w ) {
            throw w.e;
        }
        handler.endDocument();
    }
    
    // wrap SAXException into a RuntimeException so that
    // exception can pass through DOMVisitor.
    private static class SAXWrapper extends RuntimeException {
        SAXWrapper( SAXException e ) { this.e=e; }
        SAXException e;
    }
}
