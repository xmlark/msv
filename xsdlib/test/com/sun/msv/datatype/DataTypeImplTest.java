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

import java.lang.reflect.*;

/**
 * tests DataTypeImpl.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DataTypeImplTest extends TestCase
{
	public DataTypeImplTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(DataTypeImplTest.class);
	}
	
	/** tests the existence of all messages */
	public void testMessages() throws Exception
	{
		Field[] fields = com.sun.msv.datatype.DataTypeImpl.class.getDeclaredFields();
		
		for( int i=0; i<fields.length; i++ )
		{
			int mod = fields[i].getModifiers();
			if( Modifier.isStatic(mod)
			&&	Modifier.isPublic(mod)
			&&	Modifier.isFinal(mod)
			&&  fields[i].getType() == String.class )
			{
				String propertyName = (String)fields[i].get(null);
				// if the specified property doesn't exist, this will throw an error
				DataTypeImpl.localize(propertyName,new Object[]{"","","","",""});
			}
		}
	}
}
