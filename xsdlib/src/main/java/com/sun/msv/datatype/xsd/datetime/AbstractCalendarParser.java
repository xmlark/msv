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

import java.math.BigInteger;
import java.util.SimpleTimeZone;

/**
 * Parses XML Schema date/time related types into a structure.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
abstract class AbstractCalendarParser {
    private final String format;
    private final String value;
    
    private final int flen;
    private final int vlen;
    
    private int fidx;
    protected int vidx;

    // track parsed date components for day-in-month validation
    private int parsedYear = Integer.MIN_VALUE;
    private int parsedMonth = Integer.MIN_VALUE;
    private int parsedDay = Integer.MIN_VALUE;

    protected AbstractCalendarParser( String format, String value ) {
        this.format = format;
        this.value = value;
        this.flen = format.length();
        this.vlen = value.length();
    }
    
    public void parse() throws IllegalArgumentException {
        while(fidx<flen) {
            char fch = format.charAt(fidx++);
            
            if(fch!='%') {  // not a meta character
                skip(fch);
                continue;
            }
            
            // seen meta character. we don't do error check against the format
            switch(format.charAt(fidx++)) {
            case 'Y': // year
                int sign=1;
                if(peek()=='-') {
                    vidx++;
                    sign=-1;
                }
                int year = sign*parseInt(4,Integer.MAX_VALUE);
                if(year==0)
                    throw new IllegalArgumentException(value); // no year 0 in XSD
                parsedYear = year;
                setYear(year);
                break;

            case 'M': // month
                int month = parseInt(2,2);
                if(month<1 || month>12)
                    throw new IllegalArgumentException(value);
                parsedMonth = month;
                setMonth(month);
                break;

            case 'D': // days
                int day = parseInt(2,2);
                if(day<1 || day>31)
                    throw new IllegalArgumentException(value);
                parsedDay = day;
                setDay(day);
                break;
        
            case 'h': // hours
                setHours(parseInt(2,2));
                break;
    
            case 'm': // minutes
                setMinutes(parseInt(2,2));
                break;

            case 's':   // parse seconds.
                setSeconds(parseInt(2,2));
            
                if(peek()=='.') {
                    // parse fraction of a second
                    vidx++;
                    parseFractionSeconds();
                }
                break;
        
            case 'z': // time zone. missing, 'Z', or [+-]nn:nn
                char vch = peek();
                if(vch=='Z') {
                    vidx++;
                    setTimeZone(TimeZone.ZERO);
                } else
                if(vch=='+' || vch=='-') {
                    vidx++;
                    int h = parseInt(2,2);
                    skip(':');
                    int m = parseInt(2,2);
                    int totalMinutes = h*60+m;
                    if(totalMinutes>14*60)
                        throw new IllegalArgumentException(value); // timezone offset out of range
                    setTimeZone(
                        new SimpleTimeZone(totalMinutes*(vch=='+'?1:-1)*60*1000, ""/*no ID*/) );
                } else {
                    setTimeZone(TimeZone.MISSING);
                }
                break;
                
            default:
                // illegal meta character. impossible.
                throw new InternalError();
            }
        }
        
        if(vidx!=vlen)
            // some tokens are left in the input
            throw new IllegalArgumentException(value);//,vidx);

        // validate day against month/year (day-in-month check)
        if(parsedDay!=Integer.MIN_VALUE && parsedMonth!=Integer.MIN_VALUE) {
            int y = parsedYear!=Integer.MIN_VALUE ? parsedYear : 4; // default to leap year if year not specified
            int maxDay = Util.maximumDayInMonthFor(y, parsedMonth-1); // Util uses 0-based month
            if(parsedDay > maxDay)
                throw new IllegalArgumentException(value);
        }
    }
    
    private char peek() throws IllegalArgumentException {
        if(vidx==vlen)  return (char)-1;
        return value.charAt(vidx);
    }
    
    private char read() throws IllegalArgumentException {
        if(vidx==vlen)  throw new IllegalArgumentException(value);//,vidx);
        return value.charAt(vidx++);
    }
    
    private void skip(char ch) throws IllegalArgumentException {
        if(read()!=ch)  throw new IllegalArgumentException(value);//,vidx-1);
    }
    
    /**
     * Skips the extra digits.
     */
    protected final void skipDigits() {
        while(isDigit(peek()))  vidx++;
    }
    
    protected final int parseInt( int minDigits, int maxDigits ) throws IllegalArgumentException {
        int vstart = vidx;
        while( isDigit(peek()) && (vidx-vstart)<maxDigits )
            vidx++;
        if((vidx-vstart)<minDigits)
            // we are expecting more digits
            throw new IllegalArgumentException(value);//,vidx);

        // NumberFormatException is IllegalArgumentException            
//            try {
            return Integer.parseInt(value.substring(vstart,vidx));
//            } catch( NumberFormatException e ) {
//                // if the value is too long for int, NumberFormatException is thrown
//                throw new IllegalArgumentException(value,vstart);
//            }
    }
    
    /**
     * Parses the digits from <tt>vidx</tt> into a {@link BigInteger}.
     * 
     * Number of digits can be obtained by comparing the difference of
     * {@link #vidx}.
     */
    protected final BigInteger parseBigInteger( int minDigits, int maxDigits ) throws IllegalArgumentException {
        int vstart = vidx;
        while( isDigit(peek()) && (vidx-vstart)<=maxDigits )
            vidx++;
        if((vidx-vstart)<minDigits)
            // we are expecting more digits
            throw new IllegalArgumentException(value);//,vidx);

        return new BigInteger(value.substring(vstart,vidx));
    }
    

    
    private static boolean isDigit(char ch) {
        return '0'<=ch && ch<='9';
    }
    
    

    protected abstract void parseFractionSeconds();
    protected abstract void setTimeZone( java.util.TimeZone tz );
    protected abstract void setSeconds(int i);
    protected abstract void setMinutes(int i);
    protected abstract void setHours(int i);
    protected abstract void setDay(int i);
    protected abstract void setMonth(int i);
    protected abstract void setYear(int i);
}
