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
 * "gYearMonth" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#gYearMonth for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class GYearMonthType extends DateTimeBaseType
{
	public static final GYearMonthType theInstance = new GYearMonthType();
	private GYearMonthType() { super("gYearMonth"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.yearMonthTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.yearMonthTypeV();
	}
}
