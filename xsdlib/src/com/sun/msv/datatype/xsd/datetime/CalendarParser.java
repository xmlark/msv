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
import java.util.GregorianCalendar;
import java.util.SimpleTimeZone;

/**
 * Parses XML Schema date/time related types into {@link java.util.Calendar}.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class CalendarParser {
    private final String format;
    private final String value;
    
    private final int flen;
    private final int vlen;
    
    private int fidx;
    private int vidx;
    
    public static GregorianCalendar parse( String format, String value ) throws IllegalArgumentException {
        return new CalendarParser(format,value).parse();
    }
    
    private CalendarParser( String format, String value ) {
        this.format = format;
        this.value = value;
        this.flen = format.length();
        this.vlen = value.length();
    }
    
    public GregorianCalendar parse() throws IllegalArgumentException {
        GregorianCalendar cal = new GregorianCalendar();
        
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
                cal.set(Calendar.YEAR,sign*parseInt(4,Integer.MAX_VALUE));
                break;
            
            case 'M': // month
                cal.set(Calendar.MONTH,parseInt(2,2)-1); // month is 0-origin.
                break;
            
            case 'D': // days
                cal.set(Calendar.DAY_OF_MONTH,parseInt(2,2));
                break;
        
            case 'h': // hours
                cal.set(Calendar.HOUR_OF_DAY,parseInt(2,2));
                break;
    
            case 'm': // minutes
                cal.set(Calendar.MINUTE,parseInt(2,2));
                break;

            case 's':   // parse seconds.
                cal.set(Calendar.SECOND,parseInt(2,2));
            
                if(peek()=='.') {
                    // parse fraction of a second
                    vidx++;
                    cal.set(Calendar.MILLISECOND,parseInt(1,3));
                    // skip the extra digits
                    while(isDigit(peek()))  vidx++;
                }
                break;
        
            case 'z': // time zone. missing, 'Z', or [+-]nn:nn
                char vch = peek();
                if(vch=='Z') {
                    vidx++;
                    cal.setTimeZone(TimeZone.JAVA_TIME_ZONE_ZERO);
                } else
                if(vch=='+' || vch=='-') {
                    vidx++;
                    int h = parseInt(2,2);
                    skip(':');
                    int m = parseInt(2,2);
                    cal.setTimeZone(new SimpleTimeZone(
                        (h*60+m)*(vch=='+'?1:-1)*60*1000, ""/*no ID*/
                        ));
                } else {
                    cal.setTimeZone(TimeZone.JAVA_TIME_ZONE_MISSING);
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
        
        return cal;
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
    
    private int parseInt( int minDigits, int maxDigits ) throws IllegalArgumentException {
        int vstart = vidx;
        while( isDigit(peek()) && (vidx-vstart)<=maxDigits )
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
    
    private static boolean isDigit(char ch) {
        return '0'<=ch && ch<='9';
    }
}