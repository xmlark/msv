/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import org.xml.sax.SAXException;

/**
 * Receives notification of the typed content of the document.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TypedContentHandler {
	
	/**
	 * receives notification of the start of a document.
	 * 
	 * @param context
	 *		This ValidationContext object is effective through the entire document.
	 */
	void startDocument( ValidationContext context ) throws SAXException;
	
	/**
	 * receives notification of the end of a document.
	 */
	void endDocument() throws SAXException;
	
	/**
	 * receives notification of a string.
	 * 
	 * @param literal
	 *		the contents.
	 * @param type
	 *		assigned type.
	 */
	void characterChunk( String literal, Datatype type ) throws SAXException;
	
	/**
	 * receives notification of the start of an element.
	 * 
	 * If this element has attributes, the start/endAttribute methods are
	 * called after this method.
	 */
	void startElement( String namespaceURI, String localName, String qName ) throws SAXException;
	
	/**
	 * receives notification of the end of an element.
	 * 
	 * @param type
	 *		assigned type.
	 */
	void endElement( String namespaceURI, String localName, String qName, ElementExp type ) throws SAXException;

	/**
	 * receives notification of the start of an attribute.
	 * 
	 * the value of the attribute is reported through the characterChunk method.
	 */
	void startAttribute( String namespaceURI, String localName, String qName ) throws SAXException;
	
	/**
	 * receives notification of the end of an attribute.
	 * 
	 * @param type
	 *		assigned type.
	 */
	void endAttribute( String namespaceURI, String localName, String qName, AttributeExp type ) throws SAXException;

	/**
	 * this method is called after the start/endAttribute method are called
	 * for all attributes.
	 */
	void endAttributePart() throws SAXException;
}
