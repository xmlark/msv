/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier;

import org.xml.sax.Locator;

/**
 * contains information about where and how validity violation was happened.
 */
public class ValidityViolation extends org.xml.sax.SAXException
{
	/** source of the error/warning */
	public final Locator locator;
	
	/** constructor for this package */
	ValidityViolation( Locator loc, String msg )
	{
		super(msg);
		this.locator = loc;
	}
}
