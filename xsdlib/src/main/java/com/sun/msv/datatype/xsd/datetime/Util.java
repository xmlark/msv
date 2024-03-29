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

import com.sun.msv.datatype.xsd.Comparator;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SimpleTimeZone;

/**
 * date/time-related utility functions/variables.
 * 
 * @author Kohsuke KAWAGUCHI
 */
class Util {
    
    // frequently used constants
    protected static final BigInteger the4  = new BigInteger("4");
    protected static final BigInteger the10 = new BigInteger("10");
    protected static final BigInteger    the12 = new BigInteger("12");
    protected static final BigInteger    the24 = new BigInteger("24");
    protected static final BigInteger the60 = new BigInteger("60");
    protected static final BigInteger the100= new BigInteger("100");
    protected static final BigInteger the400= new BigInteger("400");
    /** number of minutes in 400 years. */
    protected static final BigInteger the210379680 = new BigInteger("210379680");
                                                                    
    protected static final BigDecimal decimal0 = new BigDecimal(BigInteger.ZERO,0);
    protected static final Integer int0 = Integer.valueOf(0);
                                                      
    protected static java.util.TimeZone timeZonePos14 = new SimpleTimeZone( 14*60*60*1000,"");
    protected static java.util.TimeZone timeZoneNeg14 = new SimpleTimeZone(-14*60*60*1000,"");
        

    /** compare two objects
     * 
     * @return    true
     *    <ul>
     *        <li> if both are null
     *        <li> if both are non-null and o1.equals(o2)
     *  </ul>
     * false otherwise.
     */
    protected static boolean objEqual( Object o1, Object o2 ) {
        if( o1==null && o2==null )    return true;
        if( o1!=null && o2!=null && o1.equals(o2))    return true;
        return false;
    }
    
    protected static int objHashCode( Object o ) {
        if(o==null)        return 0;
        else            return o.hashCode();
    }
    
    /**
     * compares two Comparable objects (possibly null) and returns
     * one of {@link Comparator} constant.
     */
    protected static int objCompare( Comparable o1, Comparable o2 ) {
        if( o1==null && o2==null )    return Comparator.EQUAL;
        if( o1!=null && o2!=null ) {
            int r = o1.compareTo(o2);
            if(r<0)        return Comparator.LESS;
            if(r>0)        return Comparator.GREATER;
            return Comparator.EQUAL;
        }
        return Comparator.UNDECIDABLE;
    }

    /** creates BigInteger that corresponds with v */
    protected static BigInteger int2bi( int v ) {
        return new BigInteger( Integer.toString(v) );
    }

    protected static BigInteger int2bi( Integer v ) {
        if( v==null )        return BigInteger.ZERO;
        return new BigInteger( v.toString() );
    }

    private static final int[] dayInMonth = new int[]{31,-1,31,30,31,30,31,  31,30,31,30,31};
    
    public static int maximumDayInMonthFor( int year, int month ) {
        if( month==1 ) {
            if( year%400 == 0 )        return 29;
            if( year%4 == 0 && year%100 != 0 )    return 29;
            return 28;
        }
        return dayInMonth[month];
    }
    
    public static int maximumDayInMonthFor( BigInteger year, int month ) {
        if( month==1 ) {// Februrary needs special care
            if( year.mod(Util.the400).intValue()==0 )    return 29;
            if( year.mod(Util.the4).intValue()==0 && year.mod(Util.the100).intValue()!=0 )    return 29;
            return 28;
        }
        
        return dayInMonth[month];
    }

}
