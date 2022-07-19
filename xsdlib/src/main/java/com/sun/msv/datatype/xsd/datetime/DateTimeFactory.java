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
 * utility functions that creates date/time related objects.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class DateTimeFactory {
    
    public static IDateTimeValueType createFromDateTime(
        Number year, Integer month, Integer day,
        Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone ) {
//        if( year instanceof Integer )
//            return new SmallDateTimeValueType( ... );
        
        BigDecimal second=null;
        
        if( year instanceof Integer )        year = new BigInteger(year.toString());
        
        if( mSecond!=null ) {
            if( mSecond instanceof Integer )    // convert it to second
                second = new BigDecimal(mSecond.toString()).movePointLeft(3);
            else
            if( mSecond instanceof BigDecimal )
                second = ((BigDecimal)mSecond).movePointLeft(3);
            else
                throw new UnsupportedOperationException();
        }
        
        return new BigDateTimeValueType( (BigInteger)year, month, day, hour, minute, second, zone );
    }
    
    public static IDateTimeValueType createFromDate(
        Number year, Integer month, Integer day, java.util.TimeZone zone ) {
        return createFromDateTime( year, month, day, null, null, null, zone );
    }
    
    public static IDateTimeValueType createFromTime(
        Integer hour, Integer minute, Number mSecond, java.util.TimeZone zone ) {
        return createFromDateTime( null, null, null, hour, minute, mSecond, zone );
    }
}
