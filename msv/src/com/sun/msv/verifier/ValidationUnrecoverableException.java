/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier;

import org.xml.sax.SAXException;

/**
 * Exception that signals error was fatal and recovery was not possible.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValidationUnrecoverableException extends SAXException
{
	public final ValidityViolation error;
	
	public ValidationUnrecoverableException( ValidityViolation vv )
	{
		super( vv.getMessage() );
		error=vv;
	}
	
	public ValidationUnrecoverableException()
	{
		super("");
		error = null;
	}
	
}
