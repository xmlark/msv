/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype.datetime;
import java.math.BigInteger;

import java.math.BigDecimal;

import junit.framework.*;

/**
 * tests TimeDurationFactory.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TimeDurationFactoryTest extends TestCase {    
	
	public TimeDurationFactoryTest(java.lang.String testName) {
		super(testName);
	}
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(TimeDurationFactoryTest.class);
	}
	
	/** Test of create method, of class com.sun.tranquilo.datatype.datetime.TimeDurationFactory. */
	public void testCreate() {
		// TODO: Add your test code here.
	}
	
}
