/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.reader.relaxng;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;

import com.sun.msv.reader.State;

/**
 * Builds DOM node from pseudo-SAX events and return it to the parent state.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AnnotationState extends State {
    // the parent state receives startElement event for this state.
    // so we have to start from one, rather than zero.
    private int depth=1;
    
    private Document document;
    private Node currentParent;

    public AnnotationState() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            document = dbf.newDocumentBuilder().newDocument();
        } catch( ParserConfigurationException e ) {
            e.printStackTrace();    // can't happen
        }
    }

    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
        depth++;
        
        Element newNode = createElement( namespaceURI, qName, atts );
        currentParent.appendChild(newNode);
        currentParent = newNode;
    }
    
    public final void endElement(String namespaceURI, String localName, String qName) {
        depth--;
        if (depth == 0) {
            ((AnnotationParent)parentState).onEndAnnotation(document.getDocumentElement());
            reader.popState();
        }

        currentParent = currentParent.getParentNode();
    }
    
    public final void endDocument() {
        throw new InternalError();  // shall never be called
    }
    
    public void characters(char[] buffer, int from, int len) {
        currentParent.appendChild(document.createTextNode(new String(buffer,from,len)));
    }


    protected void startSelf() {
        currentParent = createElement( startTag.namespaceURI, startTag.qName, startTag.attributes );
        document.appendChild(currentParent);
    }

    /**
     * Creates a new DOM element.
     */
    private Element createElement(String namespaceURI, String qname, Attributes attributes) {
        Element e = document.createElementNS(namespaceURI,qname);
        for( int i=0; i<attributes.getLength(); i++ )
            e.setAttributeNS(
                attributes.getURI(i),
                attributes.getQName(i),
                attributes.getValue(i)
            );
        return e;
    }

}