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
 * "gDay" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#gDay for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class GDayType extends DateTimeBaseType
{
	public static final GDayType theInstance = new GDayType();
	private GDayType() { super("gDay"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.dayTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.dayTypeV();
	}
}
