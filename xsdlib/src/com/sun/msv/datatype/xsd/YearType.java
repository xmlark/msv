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
 * "year" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#year for the spec
 */
public class YearType extends DateTimeBaseType
{
	public static final YearType theInstance = new YearType();
	private YearType() { super("year"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.yearTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.yearTypeV();
	}
}
