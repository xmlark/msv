/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import junit.framework.*;

import java.util.*;

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
		assertEquals( 678, o.get(o.MILLISECOND) );
		
		// time zone less.
		o = (Calendar)DateTimeType.theInstance.createJavaObject(
			"2001-01-02T03:04:05", null );
		// it should assume the local time zone.
		assertEquals(
			TimeZone.getDefault().getRawOffset(),
			o.getTimeZone().getRawOffset() );
		
		// non-GMT.
		// when California (-08:00) is 3 AM, it will be 11AM in Greenwich.
		o = (Calendar)DateTimeType.theInstance.createJavaObject(
			"2001-01-02T03:04:05.678-08:00", null );
		assertEquals( "2 Jan 2001 11:04:05 GMT", o.getTime().toGMTString() );
		assertEquals( -8*60*60*1000, o.getTimeZone().getRawOffset() );
		
	// time type.
		o = (Calendar)TimeType.theInstance.createJavaObject("08:12:30Z",null);
		assert( !o.isSet(o.YEAR) );
		assert( !o.isSet(o.MONTH) );
		assert( !o.isSet(o.DAY_OF_MONTH) );
		assertEquals( 8, o.get(o.HOUR) );
		assertEquals( 12, o.get(o.MINUTE) );
		assertEquals( 30, o.get(o.SECOND) );
	}
}
