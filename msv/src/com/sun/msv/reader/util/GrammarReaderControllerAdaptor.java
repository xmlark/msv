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
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;

/**
 * wraps GrammarReaderController by SAX ErrorHandler interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarReaderControllerAdaptor implements ErrorHandler
{
	private final GrammarReader reader;
	private final GrammarReaderController controller;
	
	/**
	 * 
	 * @param _reader
	 *		can be null. If non-null, hadError flag will be properly maintained.
	 */
	public GrammarReaderControllerAdaptor( GrammarReader _reader, GrammarReaderController _controller ) {
		this.reader = _reader;
		this.controller = _controller;
	}
	
	public GrammarReaderControllerAdaptor( GrammarReader _reader ) {
		this( _reader, _reader.controller );
	}

	
	public void fatalError( SAXParseException spe ) throws SAXException {
		error(spe);
		throw spe;
	}
	
	public void error( SAXParseException spe ) throws SAXException {
		if(reader!=null)		reader.hadError = true;
		controller.error( getLocator(spe), spe.getMessage(), spe.getException() );
	}
	
	public void warning( SAXParseException spe ) {
		controller.warning( getLocator(spe), spe.getMessage() );
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
