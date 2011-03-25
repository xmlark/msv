/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schematron.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * builds DOM from SAX2 event stream.
 */
public class DOMBuilder extends DefaultHandler {
	
	protected final DocumentBuilder builder;
	protected Document dom;
	protected Node parent;

    private StringBuffer buf = new StringBuffer();

	public DOMBuilder( DocumentBuilder builder ) {
		this.builder = builder;
	}
	public DOMBuilder() throws ParserConfigurationException {
		this( DocumentBuilderFactory.newInstance().newDocumentBuilder() );
	}
	
	/**
	 * returns DOM. This method should be called after the parsing was completed.
	 */
	public Document getDocument() {
		return dom;
	}

	public void startDocument() throws SAXException {
		parent = dom = builder.newDocument();
	}
	
	public void startElement( String ns, String local, String qname, Attributes atts ) throws SAXException {
        processText();
		Element e = dom.createElementNS( ns, local );
		parent.appendChild(e);
		parent = e;
		
		for( int i=0; i<atts.getLength(); i++ )
			e.setAttributeNS( atts.getURI(i), atts.getLocalName(i), atts.getValue(i) );
	}
	
	public void endElement( String ns, String local, String qname ) throws SAXException {
        processText();
		parent = parent.getParentNode();
	}
	
	public void characters( char[] buf, int start, int len ) throws SAXException {
        // Xalan doesn't like consequtive text nodes in a DOM tree when
        // doing XPath, so buffer them
        this.buf.append(buf,start,len);
    }

    private void processText() {
        if(buf.length()>0)
    		parent.appendChild( dom.createTextNode(buf.toString()) );
        buf.setLength(0);
	}
	
	public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
        characters(buf,start,len);
	}
}
