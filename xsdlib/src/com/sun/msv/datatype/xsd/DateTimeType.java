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
 * "dateTime" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#dateTime for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class DateTimeType extends DateTimeBaseType {
	
	public static final DateTimeType theInstance = new DateTimeType();
	private DateTimeType() {
		super("dateTime");
	}

	protected void runParserL( ISO8601Parser p ) throws Exception {
		p.dateTimeTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception {
		return p.dateTimeTypeV();
	}
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if(!(value instanceof IDateTimeValueType))
			throw new IllegalArgumentException();
		
		BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
		return	formatYear(bv.getYear())+"-"+
				formatTwoDigits(bv.getMonth(),1)+"-"+
				formatTwoDigits(bv.getDay(),1)+"T"+
				formatTwoDigits(bv.getHour())+":"+
				formatTwoDigits(bv.getMinute())+":"+
				formatSeconds(bv.getSecond())+
				formatTimeZone(bv.getTimeZone());
	}
}
