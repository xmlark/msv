/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package batch.writer;

import batch.ThrowErrorController;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.writer.GrammarWriter;

/**
 * tests RELAX NG converter.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXNGTester extends BatchWriterTester {
	
	protected void usage() {
		System.out.println(
			"usage "+this.getClass().getName()+" (relax|trex|xsd|dtd|rng) <test case directory>\n"+
			"  tests RELAX NG converter by\n"+
			"  1. converting schema files of the specified type into RELAX NG\n"+
			"  2. then parse it by RELAX NG parser\n"+
			"  3. then use the test instances to ensure the correctness\n");
	}

	public static void main( String[] av ) throws Exception {
		new RELAXNGTester().run(av);
	}

	protected GrammarReader createReader() {
		return new com.sun.msv.reader.trex.ng.RELAXNGReader(
			new ThrowErrorController(),
			factory );
	}
	protected GrammarWriter getWriter() {
		return new com.sun.msv.writer.relaxng.RELAXNGWriter();
	}
}
