/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.verifier;

import junit.framework.*;
import java.util.StringTokenizer;
import java.io.File;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGBatchTest {
	public static TestSuite suite() throws Exception {
		StringTokenizer tokens = new StringTokenizer( System.getProperty("RELAXNGBatchTestDir"), ";" );
		
		TestSuite s = new TestSuite();
		while( tokens.hasMoreTokens() ) {
			String dir = tokens.nextToken();
			BatchVerifyTester t = new BatchVerifyTester();
			t.init("rng", dir, ".rng", batch.BatchTester.genericLoader);
			s.addTest( t.suite() );
			
			// test .ssuite files
			SuiteTester	tester = new SuiteTester(new msv.IValidatorImpl());
			s.addTest(
				tester.createTestSuiteFromDir( new File(dir), ".rng.ssuite" ));
		}
		
		return s;
	}
}
