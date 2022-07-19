/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.datatype.xsd.datetime;

import java.util.Calendar;
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
    
    // this version is faster than new GregorianCalendar()
    // which involves in setting the current time.
    private final GregorianCalendar cal = new GregorianCalendar(0,0,0);
    
    private CalendarParser( String format, String value ) {
        super(format,value);
        // erase all the fields to remove any trace of the current time.
        cal.clear(Calendar.YEAR);
        cal.clear(Calendar.MONTH);
        cal.clear(Calendar.DAY_OF_MONTH);
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
