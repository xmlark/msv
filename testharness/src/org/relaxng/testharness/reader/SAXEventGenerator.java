/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.reader;

import org.w3c.dom.Node;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;
import java.util.Vector;

/**
 * produces SAX2 event from a DOM tree.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SAXEventGenerator {
	
	/**
	 * scans the specified DOM and sends SAX2 events to the handler.
	 */
	public static void parse( Document dom, ContentHandler handler ) throws SAXException {
		
		// set a dummy locator. We cannot provide location information.
		handler.setDocumentLocator( new LocatorImpl() );
		handler.startDocument();
		visit(dom.getDocumentElement(),handler);
		handler.endDocument();
	}

	private static void visit( Element e, ContentHandler handler ) throws SAXException {
		Vector prefixes = new Vector();
		
		int attLen = e.getAttributes().getLength();
		AttributesImpl atts = new AttributesImpl();
		for( int i=0; i<attLen; i++ ) {
			Attr a = (Attr)e.getAttributes().item(i);
			
			// In SAX, "" indicates the unspecified namespace.
			String ns = a.getNamespaceURI();
			if(ns==null)	ns="";
			
			// handle namespace attributes
			if( "xmlns".equals(a.getPrefix()) ) {
				prefixes.add(a.getLocalName());
				handler.startPrefixMapping( a.getLocalName(), a.getValue() );
				continue;	// do not add it to 'atts'
			}
			if( "xmlns".equals(a.getName()) ) {
				prefixes.add("");
				handler.startPrefixMapping( "", a.getValue() );
				continue;
			}
			
			atts.addAttribute( ns, a.getLocalName(),
				a.getName(), null/*no type available*/, a.getValue() );
		}
		
		String ns = e.getNamespaceURI();
		if(ns==null)	ns="";
		handler.startElement( ns, e.getLocalName(), e.getNodeName(), atts );

		NodeList lst = e.getChildNodes();
		int len = lst.getLength();
		for( int i=0; i<lst.getLength(); i++ ) {
			Node n = lst.item(i);
			if( n.getNodeType() == n.ELEMENT_NODE )
				visit( (Element)n, handler );
			else
				visitNode( n, handler );
		}

		handler.endElement( e.getNamespaceURI(), e.getLocalName(), e.getNodeName() );
		
		// call endPrefixMapping methods
		// TODO: it would be nice if we can "shuffle" the prefixes
		// so that the order of calling endPrefixMapping won't match the order of the
		// startPrefixMapping method.
		for( int i=0; i<prefixes.size(); i++ )
			handler.endPrefixMapping( (String)prefixes.get(i) );
	}
	
	private static void visitNode( Node n, ContentHandler handler ) throws SAXException {
		if( n.getNodeType()==n.TEXT_NODE
		||  n.getNodeType()==n.CDATA_SECTION_NODE ) {
			String text = n.getNodeValue();
			handler.characters( text.toCharArray(), 0, text.length() );
		}
	}
}
