package com.sun.tranquilo.datatype.datetime;
import junit.framework.*;

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
		suite.addTest(com.sun.tranquilo.datatype.datetime.TimeDurationFactoryTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.UtilTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.DateTimeFactoryTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.BigTimeDurationValueTypeTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.BigDateTimeValueTypeTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.TimeZoneTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.ISO8601ParserTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.ISO8601ParserTokenManagerTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.ParseExceptionTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.TokenTest.suite());
		suite.addTest(com.sun.tranquilo.datatype.datetime.TokenMgrErrorTest.suite());
		//:JUNIT--
		//This value MUST ALWAYS be returned from this function.
		return suite;
	}
	
}
