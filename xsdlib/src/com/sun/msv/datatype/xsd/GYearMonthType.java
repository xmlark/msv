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
 * "gYearMonth" type.
 * 
 * type of the value object is {@link IDateTimeValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#gYearMonth for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GYearMonthType extends DateTimeBaseType {
    public static final GYearMonthType theInstance = new GYearMonthType();
    private GYearMonthType() { super("gYearMonth"); }

    protected void runParserL( ISO8601Parser p ) throws Exception {
        p.yearMonthTypeL();
    }

    protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception {
        return p.yearMonthTypeV();
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof IDateTimeValueType ))
            throw new IllegalArgumentException();
        
        BigDateTimeValueType bv = ((IDateTimeValueType)value).getBigValue();
        return    formatYear(bv.getYear())+"-"+
                formatTwoDigits(bv.getMonth(),1)+
                formatTimeZone(bv.getTimeZone());
    }

    
    public String serializeJavaObject( Object value, SerializationContext context ) {
        if(!(value instanceof Calendar))    throw new IllegalArgumentException();
        Calendar cal = (Calendar)value;
        
        
        StringBuffer result = new StringBuffer();

        result.append(formatYear(cal.get(Calendar.YEAR)));
        result.append('-');
        result.append(formatTwoDigits(cal.get(Calendar.MONTH)+1));
        result.append(formatTimeZone(cal));
        
        return result.toString();
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
