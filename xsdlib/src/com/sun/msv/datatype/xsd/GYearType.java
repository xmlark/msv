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
import com.sun.msv.datatype.datetime.BigDateTimeValueType;

/**
 * "gYear" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#gYear for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class GYearType extends DateTimeBaseType
{
	public static final GYearType theInstance = new GYearType();
	private GYearType() { super("gYear"); }

	protected void runParserL( ISO8601Parser p ) throws Exception
	{
		p.yearTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception
	{
		return p.yearTypeV();
	}

	public String convertToLexicalValue( Object value ) {
		if(!(value instanceof IDateTimeValueType ))
			throw new IllegalArgumentException();
		
		BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
		return	formatYear(bv.getYear())+
				formatTimeZone(bv.getTimeZone());
	}
}
