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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * builds DOM from SAX2 event stream.
 */
public class DOMBuilder extends DefaultHandler {
	
	protected final DocumentBuilder builder;
	protected Document dom;
	protected Node parent;
	
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
		Element e = dom.createElementNS( ns, local );
		parent.appendChild(e);
		parent = e;
		
		for( int i=0; i<atts.getLength(); i++ )
			e.setAttributeNS( atts.getURI(i), atts.getLocalName(i), atts.getValue(i) );
	}
	
	public void endElement( String ns, String local, String qname ) throws SAXException {
		parent = parent.getParentNode();
	}
	
	public void characters( char[] buf, int start, int len ) throws SAXException {
		parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
	}
	
	public void ignorableWhitespace( char[] buf, int start, int len ) throws SAXException {
		parent.appendChild( dom.createTextNode(new String(buf,start,len)) );
	}
}
