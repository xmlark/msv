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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests TimeZone.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TimeZoneTest extends TestCase {    
	
	public TimeZoneTest(java.lang.String testName) {
		super(testName);
	}
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TimeZoneTest.class);
	}
	
	/** Test of create method, of class com.sun.msv.datatype.datetime.TimeZone. */
	public void testCreate()
	{
		assertSame( TimeZone.create(0), TimeZone.GMT );
		assertTrue( TimeZone.create(100).minutes==100 );
		
		// range check
		TimeZone.create(14*60);
		try
		{
			TimeZone.create(14*60+1);
			fail();
		}
		catch( IllegalArgumentException iae ) {;}
		TimeZone.create(-14*60);
		try
		{
			TimeZone.create(-14*60-1);
			fail();
		}
		catch( IllegalArgumentException iae ) {;}	
	}
	
	/** Test of hashCode method, of class com.sun.msv.datatype.datetime.TimeZone. */
	public void testHashCode()
	{
		assertEquals( TimeZone.create(50), TimeZone.create(50) );
		assertEquals( TimeZone.create(-123), TimeZone.create(-123) );
	}
	
	/** serializes o and then returns de-serialized object. */
	public Object freezeDry( Object o ) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		
		// serialize it
		oos.writeObject( o );
		oos.flush();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		return ois.readObject();
	}

	/** test serialization. */
	public void testSerialization() throws Exception {
		
		// ensure that serialization doesn't break
		assertSame( TimeZone.GMT, TimeZone.GMT );
	}
}
