/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import com.sun.msv.datatype.datetime.ISO8601Parser;
import com.sun.msv.datatype.datetime.IDateTimeValueType;

/**
 * "gMonthDay" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#gMonthDay for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class GMonthDayType extends DateTimeBaseType
{
	public static final GMonthDayType theInstance = new GMonthDayType();
	private GMonthDayType() { super("gMonthDay"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.monthDayTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.monthDayTypeV();
	}
}
