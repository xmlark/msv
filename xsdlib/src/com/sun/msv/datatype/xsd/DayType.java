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
 * "day" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#day for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class DayType extends DateTimeBaseType
{
	public static final DayType theInstance = new DayType();
	private DayType() { super("day"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.dayTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.dayTypeV();
	}
}
