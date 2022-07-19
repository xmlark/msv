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

/**
 * Formats a {@link Calendar} object to a String.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class CalendarFormatter extends AbstractCalendarFormatter {
    
    private CalendarFormatter() {} // no instanciation
    
    private static final CalendarFormatter theInstance = new CalendarFormatter();
    
    public static String format( String format, Calendar cal ) {
        return theInstance.doFormat(format,cal);
    }
    
    protected Calendar toCalendar(Object cal) {
        return (Calendar)cal;
    }

    protected void formatYear(Object cal, StringBuffer buf) {
        int year = ((Calendar)cal).get(Calendar.YEAR);
        
        String s;
        if (year <= 0) // negative value
            s = Integer.toString(1 - year);
        else // positive value
            s = Integer.toString(year);

        while (s.length() < 4)
            s = "0" + s;
        if (year <= 0)
            s = "-" + s;
        
        buf.append(s);
    }

    protected void formatMonth(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.MONTH)+1,buf);
    }

    protected void formatDays(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.DAY_OF_MONTH),buf);
    }

    protected void formatHours(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.HOUR_OF_DAY),buf);
    }

    protected void formatMinutes(Object cal, StringBuffer buf) {
        formatTwoDigits(((Calendar)cal).get(Calendar.MINUTE),buf);
    }

    protected void formatSeconds(Object _cal, StringBuffer buf) {
        Calendar cal = (Calendar)_cal;
        formatTwoDigits(cal.get(Calendar.SECOND),buf);
        if (cal.isSet(Calendar.MILLISECOND)) { // milliseconds
            int n = cal.get(Calendar.MILLISECOND);
            if(n!=0) {
                String ms = Integer.toString(n);
                while (ms.length() < 3)
                    ms = "0" + ms; // left 0 paddings.

                buf.append('.');
                buf.append(ms);
            }
        }
    }

}
