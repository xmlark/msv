/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader;

import org.xml.sax.Locator;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * Event notification interface for controlling grammar parsing process.
 * 
 * <ol>
 *  <li>receives notification of errors and warnings while parsing a grammar
 *  <li>controls how inclusion of other grammars are processed.
 * </ol>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface GrammarReaderController
{
	void warning( Locator[] locs, String errorMessage );
	void error( Locator[] locs, String errorMessage, Exception nestedException );

	/**
	 * controls inclusion.
	 * 
	 * @return
	 *		return null for the default handling ( new InputSource(url) ).
	 *		Otherwise returned InputSource is used.
	 */
	InputSource resolveInclude( String url ) throws SAXException,IOException;
}
