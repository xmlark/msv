/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.verifier;

import org.iso_relax.dispatcher.Dispatcher;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ErrorHandler;
import com.sun.msv.verifier.ValidityViolation;

/**
 * wraps ISORELAX ErrorHandler by VerificationErrorHandler interface.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ErrorHandlerAdaptor implements com.sun.msv.verifier.VerificationErrorHandler
{
	private final Dispatcher core;
	
	public ErrorHandlerAdaptor( Dispatcher core ) {
		this.core = core;
	}
	
	public void onError( ValidityViolation error ) throws SAXException {
		core.getErrorHandler().error( convertToSAXParseException(error) );
	}

	public void onWarning( ValidityViolation error ) throws SAXException {
		core.getErrorHandler().warning( convertToSAXParseException(error) );
	}
	
	protected static final SAXParseException convertToSAXParseException(
		ValidityViolation vv ) {
		return new SAXParseException( vv.getMessage(), vv.locator );
	}
}
