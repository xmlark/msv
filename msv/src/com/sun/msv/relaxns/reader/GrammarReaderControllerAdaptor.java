/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.relaxns.reader;

import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;
import com.sun.tranquilo.reader.GrammarReaderController;

/**
 * wraps GrammarReaderController by ISORELAX ErrorHandler interface.
 */
public class GrammarReaderControllerAdaptor implements ErrorHandler
{
	private final GrammarReaderController core;
	
	public GrammarReaderControllerAdaptor( GrammarReaderController core ) {
		this.core = core;
	}
	
	public void fatalError( SAXParseException spe ) {
		core.error( getLocator(spe), spe.getMessage(), spe.getException() );
	}
	
	public void error( SAXParseException spe ) {
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
