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

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.relaxng.testharness.model.XMLDocument;
import org.relaxng.testharness.model.RNGHeader;

/**
 * {@link XMLDocument} implemented by a DOM tree.
 * 
 * This implementation keeps the document as a DOM tree.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class XMLDocumentImpl implements XMLDocument
{
	XMLDocumentImpl( Document dom ) {
		this.dom = dom;
	}
	
	private Document dom;
	
	public Document getAsDOM() { return dom; }
	
	public void getAsSAX( ContentHandler handler ) throws SAXException {
		SAXEventGenerator.parse( dom, handler );
	}

	public InputSource getAsInputSource() throws Exception {
		return null;
	}
	
	/**
	 * No header information is avaiable.
	 */
	public RNGHeader getHeader() { return null; }
}
