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

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

/**
 * contains information about where and how validity violation was happened.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ValidityViolation extends SAXParseException
{
	/** constructor for this package */
	public ValidityViolation( Locator loc, String msg ) {
		super( msg, loc );
	}
}
