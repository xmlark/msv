package com.sun.tranquilo.datatype;

import junit.framework.*;
import java.lang.reflect.*;

public class BadTypeExceptionTest extends TestCase
{
	public BadTypeExceptionTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(BadTypeExceptionTest.class);
	}

	/** tests that every error message defined in the class is actually written in property file.
	 *
	 * TODO: multi-lingual support.
	 */
	public void testResource() throws Exception
	{
		Field[] fields = com.sun.tranquilo.datatype.BadTypeException.class.getDeclaredFields();
		
		for( int i=0; i<fields.length; i++ )
			if( fields[i].getName().startsWith("ERR_") )
			{
				String propertyName = (String)fields[i].get(null);
				// if the specified property doesn't exist, this will throw an error
				new BadTypeException( propertyName, "", "", "" /** dummy parameters */ );
			}
	}
}