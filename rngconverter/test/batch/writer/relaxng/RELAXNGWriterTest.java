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
	public static TestSuite suite() throws Exception {
		TestSuite s = new TestSuite();

		append( s, "RELAXBatchTestDir", "relax" );
		append( s, "TREXBatchTestDir", "trex" );
		append( s, "XSDBatchTestDir", "xsd" );
		append( s, "DTDBatchTestDir", "dtd" );
		return s;
	}
	
	private static void append( TestSuite s, String propName, String target ) throws Exception {
		s.addTest( new RELAXNGTester().createFromProperty(target,propName) );
	}
}
