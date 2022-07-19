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

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PreciseCalendarParser extends AbstractCalendarParser {
    public static BigDateTimeValueType parse( String format, String value ) throws IllegalArgumentException {
        PreciseCalendarParser parser = new PreciseCalendarParser(format,value);
        parser.parse();
        return parser.createCalendar();
    }

    private PreciseCalendarParser( String format, String value ) {
        super(format,value);
    }
    
    private BigDateTimeValueType createCalendar() {
        return new BigDateTimeValueType(year,month,day,hour,minute,second,timeZone);
    }
    
    
    private BigInteger year;
    private Integer month;
    private Integer day;
    private Integer hour;
    private Integer minute;
    private BigDecimal second;
    private java.util.TimeZone timeZone;
        

    protected void parseFractionSeconds() {
        int s = vidx;
        BigInteger bi = parseBigInteger(1,Integer.MAX_VALUE);
        BigDecimal d = new BigDecimal(bi,vidx-s);
        if( second==null)   second = d;
        else                second = second.add(d);
    }

    protected void setTimeZone(java.util.TimeZone tz) {
        if(tz==TimeZone.MISSING)    tz=null;
        this.timeZone = tz;
    }

    protected void setSeconds(int i) {
        BigDecimal d = new BigDecimal(BigInteger.valueOf(i));
        if( second==null)   second = d;
        else                second = second.add(d);
    }

    protected void setMinutes(int i) {
        minute = new Integer(i);
    }

    protected void setHours(int i) {
        hour = new Integer(i);
    }

    protected void setDay(int i) {
        day = new Integer(i-1);     // zero origin
    }

    protected void setMonth(int i) {
        month = new Integer(i-1);   // zero origin
    }

    protected void setYear(int i) {
        year = BigInteger.valueOf(i);
    }
    
}
