/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import java.util.Calendar;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.ISO8601Parser;

/**
 * "dateTime" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#dateTime for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
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
	
	public String serializeJavaObject( Object value, SerializationContext context ) {
		if(!(value instanceof Calendar))	throw new IllegalArgumentException();
		Calendar cal = (Calendar)value;
		
		
		StringBuffer result = new StringBuffer();

		result.append(formatYear(cal.get(Calendar.YEAR)));
		result.append('-');
		result.append(formatTwoDigits(cal.get(Calendar.MONTH)+1));
		result.append('-');
		result.append(formatTwoDigits(cal.get(Calendar.DAY_OF_MONTH)));
		result.append('T');
		result.append(formatTwoDigits(cal.get(Calendar.HOUR_OF_DAY)));
		result.append(':');
		result.append(formatTwoDigits(cal.get(Calendar.MINUTE)));
		result.append(':');
		result.append(formatSeconds(cal));
		result.append(formatTimeZone(cal));

		return result.toString();
	}

    // serialization support
    private static final long serialVersionUID = 1;    
}
