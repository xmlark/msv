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
 * "gMonth" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#gMonth for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class GMonthType extends DateTimeBaseType
{
	public static final GMonthType theInstance = new GMonthType();
	private GMonthType() { super("gMonth"); }

	protected void runParserL( ISO8601Parser p ) throws Exception {
		p.monthTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception {
		return p.monthTypeV();
	}
	
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if(!(value instanceof IDateTimeValueType ))
			throw new IllegalArgumentException();
		
		BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
		return	"--"+
				formatTwoDigits(bv.getMonth(),1)+"--"+
				formatTimeZone(bv.getTimeZone());
	}
}
