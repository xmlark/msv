/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.junit;

import org.relaxng.testharness.validator.IValidator;
import junit.framework.TestSuite;
import org.xml.sax.InputSource;

/**
 * Test runner.
 * 
 * This class can be used as an utility class to kick the test.
 * Depending on the situation, you may want to implement another driver.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestRunner
{
	public static void main( String args[] ) throws Exception {
		
		if( args.length<2 ) {
			System.out.println(
				"Usage: TestRunner <IValidatorImpl class name> <test suite file> ...\n" );
			return;
		}
		
		// load the IValidator implementation to be tested
		IValidator validator = (IValidator)Class.forName(args[0]).newInstance();
		
		// collect test suites
		TestSuite suite = new TestSuite();
		for( int i=1; i<args.length; i++ )
			suite.addTest( TestSuiteBuilder.create( new InputSource(args[i]), validator ) );
		
		// run the test
		junit.textui.TestRunner.run(suite);
		
	}
}
