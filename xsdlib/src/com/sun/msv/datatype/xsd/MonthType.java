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
 * "month" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#month for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class MonthType extends DateTimeBaseType
{
	public static final MonthType theInstance = new MonthType();
	private MonthType() { super("month"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.monthTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.monthTypeV();
	}
}
