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

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * signals unexpected test result.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnexpectedResultException extends Exception
{
	public final XSDatatype type;
	public final String baseTypeName;
	public final String testInstance;
	public final boolean supposedToBeValid;
	public final TypeIncubator incubator;

	UnexpectedResultException( XSDatatype typeObj, String baseTypeName,
		String testInstance, boolean supposedToBeValid, TypeIncubator ti )
	{
		this.type				= typeObj;
		this.baseTypeName		= baseTypeName;
		this.testInstance		= testInstance;
		this.supposedToBeValid	= supposedToBeValid;
		this.incubator			= ti;
	}
}
