/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.writer.relaxng;

import junit.framework.*;
import java.util.StringTokenizer;
import batch.writer.*;

/**
 * tests the RELAXNGWriter with the multiple test directories.
 * 
 * for use by automated test by ant.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGWriterTest {
	public static TestSuite suite() {
		TestSuite s = new TestSuite();

		append( s, "RELAXBatchTestDir", "relax", ".rlx", batch.BatchTester.genericLoader );
		append( s, "TREXBatchTestDir", "trex", ".trex", batch.BatchTester.genericLoader );
//		append( s, "XSDBatchTestDir", "xsd", ".xsd", batch.BatchTester.genericLoader );
		append( s, "DTDBatchTestDir", "dtd", ".dtd", batch.BatchTester.dtdLoader );
		return s;
	}
	
	private static void append( TestSuite s, String propName, String target, String ext, batch.BatchTester.Loader loader ) {
		StringTokenizer tokens = new StringTokenizer( System.getProperty(propName), ";" );
		
		while( tokens.hasMoreTokens() ) {
			BatchWriterTester t = new RELAXNGTester();
			t.init( target, tokens.nextToken(), ext, loader );
			s.addTest( t.suite() );
		}
	}
}
