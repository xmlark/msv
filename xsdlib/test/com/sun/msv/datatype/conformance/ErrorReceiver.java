/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype.conformance;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.BadTypeException;
import com.sun.tranquilo.datatype.TypeIncubator;

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
	boolean reportTestCaseError( DataType baseType, TypeIncubator incubator,
		BadTypeException e );
}