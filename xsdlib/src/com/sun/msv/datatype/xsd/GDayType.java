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
import java.util.Calendar;

/**
 * "gDay" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#gDay for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GDayType extends DateTimeBaseType {
	public static final GDayType theInstance = new GDayType();
	private GDayType() { super("gDay"); }

	protected void runParserL( ISO8601Parser p ) throws Exception {
		p.dayTypeL();
	}

	protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception {
		return p.dayTypeV();
	}

	public String convertToLexicalValue( Object value, SerializationContext context ) {
		if(!(value instanceof IDateTimeValueType ))
			throw new IllegalArgumentException();
		
		BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
		return	"---" + formatTwoDigits(bv.getDay(),1)+
				formatTimeZone(bv.getTimeZone());
	}
	
	public String serializeJavaObject( Object value, SerializationContext context ) {
		if(!(value instanceof Calendar))	throw new IllegalArgumentException();
		Calendar cal = (Calendar)value;
		
		
		StringBuffer result = new StringBuffer();

		result.append("---");
		result.append(formatTwoDigits(cal.get(cal.DAY_OF_MONTH)));
		result.append(formatTimeZone(cal));
		
		return result.toString();
	}
}
