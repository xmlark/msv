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

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.datetime.ISO8601Parser;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import org.relaxng.datatype.ValidationContext;
import java.util.Calendar;

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

		result.append(formatYear(cal.get(cal.YEAR)));
		result.append('-');
		result.append(formatTwoDigits(cal.get(cal.MONTH)));
		result.append('-');
		result.append(formatTwoDigits(cal.get(cal.DAY_OF_MONTH)));
		result.append('T');
		result.append(formatTwoDigits(cal.get(cal.HOUR_OF_DAY)));
		result.append(':');
		result.append(formatTwoDigits(cal.get(cal.MINUTE)));
		result.append(':');
		result.append(formatTwoDigits(cal.get(cal.SECOND)));
		if( cal.isSet(cal.MILLISECOND) ) {// milliseconds
			String ms = Integer.toString(cal.get(cal.MILLISECOND));
			while(ms.length()<3)	ms = "0"+ms;	// left 0 paddings.
			
			result.append('.');
			result.append(ms);
		}
		
		result.append(formatTimeZone(cal));
		
		return result.toString();
	}
}
