/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

import com.sun.tranquilo.datatype.datetime.ISO8601Parser;
import com.sun.tranquilo.datatype.datetime.IDateTimeValueType;

/**
 * "time" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#time for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class TimeType extends DateTimeBaseType
{
	public static final TimeType theInstance = new TimeType();
	private TimeType() { super("time"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.timeTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.timeTypeV();
	}
}
