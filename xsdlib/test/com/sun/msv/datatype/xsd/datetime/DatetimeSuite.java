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
import junit.framework.*;

/**
 * tests all test cases in this package.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DatetimeSuite extends TestCase {    
	
	public DatetimeSuite(java.lang.String testName) {
		super(testName);
	}
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		//--JUNIT:
		//This block was automatically generated and can be regenerated again.
		//Do NOT change lines enclosed by the --JUNIT: and :JUNIT-- tags.
		TestSuite suite = new TestSuite("DatetimeSuite");
		suite.addTest(com.sun.msv.datatype.xsd.datetime.TimeDurationFactoryTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.UtilTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.DateTimeFactoryTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.BigTimeDurationValueTypeTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.BigDateTimeValueTypeTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.TimeZoneTest.suite());
		suite.addTest(com.sun.msv.datatype.xsd.datetime.ISO8601ParserTest.suite());
		//:JUNIT--
		//This value MUST ALWAYS be returned from this function.
		return suite;
	}
	
}
