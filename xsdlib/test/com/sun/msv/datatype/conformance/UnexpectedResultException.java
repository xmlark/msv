/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.conformance;

import com.sun.msv.datatype.*;

/**
 * signals unexpected test result.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnexpectedResultException extends Exception
{
	public final DataType type;
	public final String baseTypeName;
	public final String testInstance;
	public final boolean supposedToBeValid;
	public final TypeIncubator incubator;

	UnexpectedResultException( DataType typeObj, String baseTypeName,
		String testInstance, boolean supposedToBeValid, TypeIncubator ti )
	{
		this.type			= typeObj;
		this.baseTypeName		= baseTypeName;
		this.testInstance		= testInstance;
		this.supposedToBeValid	= supposedToBeValid;
		this.incubator			= ti;
	}
}
