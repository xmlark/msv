/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.util;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.msv.reader.GrammarReaderController;

/**
 * wraps GrammarReaderController by SAX ErrorHandler interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarReaderControllerAdaptor implements ErrorHandler
{
	private final GrammarReaderController core;
	
	public GrammarReaderControllerAdaptor( GrammarReaderController core ) {
		this.core = core;
	}
	
	public void fatalError( SAXParseException spe ) throws SAXException {
		core.error( getLocator(spe), spe.getMessage(), spe.getException() );
		throw spe;
	}
	
	public void error( SAXParseException spe ) throws SAXException {
		core.error( getLocator(spe), spe.getMessage(), spe.getException() );
	}
	
	public void warning( SAXParseException spe ) {
		core.warning( getLocator(spe), spe.getMessage() );
	}
			
	protected Locator[] getLocator( SAXParseException spe ) {
		LocatorImpl loc = new LocatorImpl();
		loc.setColumnNumber( spe.getColumnNumber() );
		loc.setLineNumber( spe.getLineNumber() );
		loc.setSystemId( spe.getSystemId() );
		loc.setPublicId( spe.getPublicId() );
		
		return new Locator[]{loc};
	}
}
