package com.sun.tranquilo.datatype.datetime;
import junit.framework.*;

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
	
	/** Test of create method, of class com.sun.tranquilo.datatype.datetime.TimeZone. */
	public void testCreate()
	{
		assertSame( TimeZone.create(0), TimeZone.GMT );
		assert( TimeZone.create(100).minutes==100 );
		
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
	
	/** Test of hashCode method, of class com.sun.tranquilo.datatype.datetime.TimeZone. */
	public void testHashCode()
	{
		assertEquals( TimeZone.create(50), TimeZone.create(50) );
		assertEquals( TimeZone.create(-123), TimeZone.create(-123) );
	}
}
