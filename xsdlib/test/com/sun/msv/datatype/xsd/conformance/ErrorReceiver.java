/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.conformance;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * receives conformance test error.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
interface ErrorReceiver
{
	/**
	 * when unexpected result is encountered, this method is called
	 *
	 * return true to abort test
	 */
	boolean report( UnexpectedResultException exp );
	
	/**
	 * when derivation by restriction failed, this method is called
	 *
	 * return true to abort test
	 */
	boolean reportTestCaseError( XSDatatype baseType, TypeIncubator incubator,
		DatatypeException e );
}
