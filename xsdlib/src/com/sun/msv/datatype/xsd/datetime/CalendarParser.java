/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.datetime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Parses XML Schema date/time related types into {@link java.util.Calendar}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class CalendarParser extends AbstractCalendarParser {
    public static GregorianCalendar parse( String format, String value ) throws IllegalArgumentException {
        CalendarParser parser = new CalendarParser(format,value);
        parser.parse();
        return parser.cal;
    }
    
    private final GregorianCalendar cal = new GregorianCalendar();
    
    private static final Date date0 = new Date(0);
    
    private CalendarParser( String format, String value ) {
        super(format,value);
        // erase all the fields to remove any trace of the current time.
        // cal.setTimeInMillis(0); -- this requires JDK 1.4
        cal.setTime(date0); // but this should be equivalent
        cal.clear();
    }
    
    protected void parseFractionSeconds() {
        cal.set(Calendar.MILLISECOND,parseInt(1,3));
        skipDigits();
    }
    
    protected void setTimeZone( java.util.TimeZone tz ) {
        cal.setTimeZone(tz);
    }

    protected void setSeconds(int i) {
        cal.set(Calendar.SECOND,i);
    }

    protected void setMinutes(int i) {
        cal.set(Calendar.MINUTE,i);
    }

    protected void setHours(int i) {
        cal.set(Calendar.HOUR_OF_DAY,i);
    }

    protected void setDay(int i) {
        cal.set(Calendar.DAY_OF_MONTH,i);
    }

    protected void setMonth(int i) {
        cal.set(Calendar.MONTH,i-1); // month is 0-origin.
    }

    protected void setYear(int i) {
        cal.set(Calendar.YEAR,i);
    }
}