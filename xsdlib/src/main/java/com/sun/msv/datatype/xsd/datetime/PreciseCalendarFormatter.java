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
import java.util.Calendar;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class PreciseCalendarFormatter extends AbstractCalendarFormatter {
    
    private PreciseCalendarFormatter() {} // no instanciation
    
    private static final PreciseCalendarFormatter theInstance = new PreciseCalendarFormatter();
    
    public static String format( String format, IDateTimeValueType cal ) {
        return theInstance.doFormat(format,cal.getBigValue());
    }
    
    protected Calendar toCalendar(Object cal) {
        return ((BigDateTimeValueType)cal).toCalendar();
    }

    protected void formatYear(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        BigInteger year = bv.getYear();
        if(year==null) {
            buf.append("0000");
            return;
        }
            
        String s;
        if (year.signum() <= 0) {
            // negative value
            buf.append('-');
            s = year.negate().add(BigInteger.ONE).toString();
        } else
            // positive value
            s = year.toString();

        while (s.length() < 4)
            s = "0" + s;
        
        buf.append(s);
    }
    
    
    protected void formatMonth(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        formatTwoDigits(bv.getMonth(),1,buf);
    }

    protected void formatDays(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        formatTwoDigits(bv.getDay(),1,buf);
    }

    protected void formatHours(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        formatTwoDigits(bv.getHour(),buf);
    }

    protected void formatMinutes(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        formatTwoDigits(bv.getMinute(),buf);
    }

    protected void formatSeconds(Object cal, StringBuffer buf) {
        BigDateTimeValueType bv = ((IDateTimeValueType)cal).getBigValue();
        BigDecimal sec = bv.getSecond();
        
        if (sec == null) {
            buf.append("00");
            return;
        }
        
        // truncate unnecesary 0s.
        while( sec.scale()>0 && sec.toString().endsWith("0") )
            sec = sec.movePointLeft(1);

        String s = sec.toString();
        if (sec.compareTo(new java.math.BigDecimal("10")) < 0)
            s = "0" + s;
        buf.append(s);
    }
    
    
    
    
    private void formatTwoDigits(Integer v,StringBuffer buf) {
        formatTwoDigits(v,0,buf);
    }

    /** formats Integer into two-character-wide string. */
    private void formatTwoDigits(Integer v, int offset,StringBuffer buf) {
        if (v == null)
            buf.append("00");
        else
            formatTwoDigits(v.intValue() + offset,buf);
    }
}
