package com.sun.msv.generator;

import junit.framework.TestSuite;


/**
 * command line driver. Useful for hand-debugging.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GeneratorTester extends BatchTester {

	public static void main( String[] av ) throws Exception {
		new GeneratorTester().run(av);
	}
	
	/** gets a TestSuite that loads and verifies all test instances in the test directory. */
	protected void populateSuite( TestSuite suite, String[] schemas ) {
		// each schema will have its own suite.
		if( schemas!=null )
			for( int i=0; i<schemas.length; i++ )
				suite.addTest( new GeneratorSuite(this,schemas[i]).suite() );
	}
}