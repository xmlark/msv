/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.datetime;
import java.math.BigInteger;

import java.math.BigDecimal;

import junit.framework.*;

/**
 * tests DateTimeFactory.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DateTimeFactoryTest extends TestCase {    
	
	public DateTimeFactoryTest(java.lang.String testName) {
		super(testName);
	}
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(DateTimeFactoryTest.class);
	}
	
	public static void testCreateFromTime()
	{
		// 1 second
		IDateTimeValueType sec1 = DateTimeFactory.
			createFromTime(null,null,new Integer(1000),null);
		assertEquals( sec1,
					  new BigDateTimeValueType(null, null, null, null, null, new BigDecimal("1"), null ) );
	}
}
