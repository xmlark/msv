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
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
abstract class AbstractCalendarFormatter {
    public String doFormat( String format, Object cal ) throws IllegalArgumentException {
        int fidx = 0;
        int flen = format.length();
        StringBuffer buf = new StringBuffer();
        
        while(fidx<flen) {
            char fch = format.charAt(fidx++);
            
            if(fch!='%') {  // not a meta character
                buf.append(fch);
                continue;
            }
            
            // seen meta character. we don't do error check against the format
            switch (format.charAt(fidx++)) {
            case 'Y' : // year
                formatYear(cal, buf);
                break;

            case 'M' : // month
                formatMonth(cal, buf);
                break;

            case 'D' : // days
                formatDays(cal, buf);
                break;

            case 'h' : // hours
                formatHours(cal, buf);
                break;

            case 'm' : // minutes
                formatMinutes(cal, buf);
                break;

            case 's' : // parse seconds.
                formatSeconds(cal, buf);
                break;

            case 'z' : // time zone
                formatTimeZone(cal,buf);
                break;
                
            default :
                // illegal meta character. impossible.
                throw new InternalError();
            }
        }
        
        return buf.toString();
    }

    
    protected abstract Calendar toCalendar( Object cal );
    protected abstract void formatYear( Object cal, StringBuffer buf );
    protected abstract void formatMonth( Object cal, StringBuffer buf );
    protected abstract void formatDays( Object cal, StringBuffer buf );
    protected abstract void formatHours( Object cal, StringBuffer buf );
    protected abstract void formatMinutes( Object cal, StringBuffer buf );
    protected abstract void formatSeconds( Object cal, StringBuffer buf );
    
    /** formats time zone specifier. */
    private void formatTimeZone(Object _cal,StringBuffer buf) {
        Calendar cal = toCalendar(_cal);
        java.util.TimeZone tz = cal.getTimeZone();

        // TODO: is it possible for the getTimeZone method to return null?
        if (tz == null)      return;
        
        // look for special instances
        if(tz==TimeZone.MISSING)    return;
        if(tz==TimeZone.ZERO) {
            buf.append('Z');
            return;
        }
        
        // otherwise print out normally.
        int offset;
        if (tz.inDaylightTime(cal.getTime())) {
            offset = tz.getRawOffset() + (tz.useDaylightTime()?3600000:0);
        } else {
            offset = tz.getRawOffset();
        }

        if (offset >= 0)
            buf.append('+');
        else {
            buf.append('-');
            offset *= -1;
        }

        offset /= 60 * 1000; // offset is in milli-seconds

        formatTwoDigits(offset / 60, buf);
        buf.append(':');
        formatTwoDigits(offset % 60, buf);
    }
    
    /** formats Integer into two-character-wide string. */
    protected final void formatTwoDigits(int n,StringBuffer buf) {
        // n is always non-negative.
        if (n < 10) buf.append('0');
        buf.append(n);
    }
    
}
