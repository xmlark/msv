/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;

/**
 * tests DateTimeBaseType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DateTimeBaseTypeTest extends TestCase
{
    public DateTimeBaseTypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DateTimeBaseTypeTest.class);
    }
    
    public void testCreateJavaObject() throws Exception {
        Calendar o;
        
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05.678Z", null );
        assertEquals( "2 Jan 2001 03:04:05 GMT", o.getTime().toGMTString() );
        assertEquals( 0, o.getTimeZone().getRawOffset() );
        assertEquals( 678, o.get(Calendar.MILLISECOND) );
        
        // time zone less.
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05", null );
        // 
        assertSame( com.sun.msv.datatype.xsd.datetime.TimeZone.MISSING,
            o.getTimeZone() );
        
        // non-GMT.
        // when California (-08:00) is 3 AM, it will be 11AM in Greenwich.
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05.678-08:00", null );
        assertEquals( "2 Jan 2001 11:04:05 GMT", o.getTime().toGMTString() );
        assertEquals( -8*60*60*1000, o.getTimeZone().getRawOffset() );
        
    // time type.
        o = (Calendar)TimeType.theInstance.createJavaObject("08:12:30Z",null);
        assertTrue( !o.isSet(Calendar.YEAR) );
        assertTrue( !o.isSet(Calendar.MONTH) );
        assertTrue( !o.isSet(Calendar.DAY_OF_MONTH) );
        assertEquals( 8, o.get(Calendar.HOUR) );
        assertEquals( 12, o.get(Calendar.MINUTE) );
        assertEquals( 30, o.get(Calendar.SECOND) );
    }
}
