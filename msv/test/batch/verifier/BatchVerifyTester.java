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

import batch.model.TestReader;

/**
 * validates instances by schemata.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BatchVerifyTester extends batch.BatchTester {
	
    protected TestReader createReader() {
        return new TestReader( new TestBuilderImpl(validator) );
    }
    
	public static void main( String[] av ) throws Exception {
		new BatchVerifyTester().run(av);
	}

	protected void usage() {
		System.out.println(
			"usage "+this.getClass().getName()+" (relax|trex|xsd|dtd|rng) [-strict] [-recursive] <test case directory>\n"+
			"  tests the validation engine by using schema files and test instances\n"+
			"  in the specified directory.");
	}
}
