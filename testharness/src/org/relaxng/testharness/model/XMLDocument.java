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
import org.xml.sax.InputSource;

/**
 * represents one XML document.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface XMLDocument
{
	/**
	 * Gets a header information associated with this document.
	 * 
	 * @return
	 *		null if no header information is available.
	 */
	RNGHeader getHeader();
	
	/**
	 * Retrieves the document as a DOM tree.
	 * 
	 * The caller may not modified the returned tree since it may be shared by
	 * multiple clients.
	 */
	Document getAsDOM() throws Exception;
	
	/**
	 * Retrieves the document as an InputSource
	 */
	InputSource getAsInputSource() throws Exception;
	
	
	/** Retrieves the document as SAX events. */
	void getAsSAX( ContentHandler handler ) throws Exception;
}
