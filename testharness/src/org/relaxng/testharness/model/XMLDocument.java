/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.model;

import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * represents one XML document.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface XMLDocument
{
	/**
	 * obtains the document as a DOM tree.
	 * 
	 * The caller may not modified the returned tree since it may be shared by
	 * multiple clients.
	 */
	Document getAsDOM();
	
	/** obtains the document as SAX events. */
	void getAsSAX( ContentHandler handler ) throws SAXException;
	
	/** obtains the title of this document, if any. */
	String getTitle();
}
