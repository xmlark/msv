/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.sm;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.msv.datatype.DatabindableDatatype;

/**
 * marshalls objects into DOM.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DOMMarshaller implements Marshaller {
	
	/**
	 * creates DOMMarshaller by using the default DOM implementation.
	 */
	public DOMMarshaller() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		
		document = factory.newDocumentBuilder().newDocument();
		parent = document;
	}
	
	/**
	 * creates DOMMarshaller by specifing the DOM object to be used.
	 * 
	 * @param emptyDom
	 *		an empty Document object. Marshalled result is created under
	 *		this document.
	 */
	public DOMMarshaller( Document emptyDom ) {
		// DOM must be empty.
		if(emptyDom.getChildNodes().getLength()!=0)
			throw new IllegalArgumentException();
		
		document = emptyDom;
		parent = document;
	}
	
	/** gets the marshalled result. */
	public Document getResult() {
		return document;
	}

	/** DOM root object of the produced XML. */
	private final Document document;
	/** the current context node. Document, Attribute or Element */
	private Node parent;

	public void startElement( String namespaceURI, String localName ) {
		Element e = document.createElementNS( namespaceURI, localName );
		parent.appendChild(e);
		parent = e;
	}
	
	public void endElement( String namespaceURI, String localName ) {
		parent = parent.getParentNode();
	}

	public void startAttribute( String namespaceURI, String localName ) {
		Attr a = document.createAttributeNS(namespaceURI,localName);
		((Element)parent).setAttributeNodeNS(a);
		parent = a;
	}
	
	public void endAttribute( String namespaceURI, String localName ) {
		parent = ((Attr)parent).getOwnerElement();
	}
	
	public void data( Object data, DatabindableDatatype type ) {
		// TODO: we should use the type object to convert data into XML representation.
		parent.appendChild( document.createTextNode(data.toString()) );
	}
}
