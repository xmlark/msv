/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.util;

import com.sun.tranquilo.verifier.VerificationErrorHandler;
import com.sun.tranquilo.verifier.ValidityViolation;
import org.xml.sax.SAXException;

/**
 * default implementation of VerificationErrorHandler.
 * 
 * If an error is found, throw ValidityViolation to stop further validation.
 * warnings are ignored.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class VerificationErrorHandlerImpl implements VerificationErrorHandler
{
	public void onError( ValidityViolation error ) throws SAXException
	{ throw error; }
	public void onWarning( ValidityViolation warning )
	{}
}
