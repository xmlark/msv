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

import com.sun.msv.datatype.xsd.datetime.ISO8601Parser;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.SerializationContext;

/**
 * "time" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#time for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TimeType extends DateTimeBaseType {
	
	public static final TimeType theInstance = new TimeType();
	private TimeType() { super("time"); }

	protected void runParserL( ISO8601Parser p ) throws Exception {
		p.timeTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception {
		return p.timeTypeV();
	}
	
	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if(!(value instanceof IDateTimeValueType))
			throw new IllegalArgumentException();
		
		BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
		return	formatTwoDigits(bv.getHour())+":"+
				formatTwoDigits(bv.getMinute())+":"+
				formatSeconds(bv.getSecond())+
				formatTimeZone(bv.getTimeZone());
	}

	
	public String serializeJavaObject( Object value, SerializationContext context ) {
		if(!(value instanceof Calendar))	throw new IllegalArgumentException();
		Calendar cal = (Calendar)value;
		
		
		StringBuffer result = new StringBuffer();

		result.append(formatTwoDigits(cal.get(cal.HOUR_OF_DAY)));
		result.append(':');
		result.append(formatTwoDigits(cal.get(cal.MINUTE)));
		result.append(':');
		result.append(formatSeconds(cal));
		
		result.append(formatTimeZone(cal));
		
		return result.toString();
	}
}
