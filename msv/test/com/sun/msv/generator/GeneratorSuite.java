/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import junit.framework.*;
import java.io.File;
import java.io.FilenameFilter;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;

/**
 * tests generator with given schema instance.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class GeneratorSuite extends TestCase {
	
	GeneratorSuite( GeneratorTester parent, String schemaFileName ) {
		super("testLoadSchema("+schemaFileName+")");
		this.parent = parent;
		this.schemaFileName = schemaFileName;
	}
	
	protected final GeneratorTester parent;
	protected final String schemaFileName;
		
	public TestSuite suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(this);	// this object itself will load schema as a test.
		return suite;
	}
		
	/** set by testLoadSchema method */
	protected TREXDocumentDeclaration docDecl;
		
	/** loads schema and generates test cases by using it. */
	protected void runTest() throws Exception {
		
		final String pathName = parent.dir + File.separatorChar + schemaFileName;
			
		// load grammar
		if( pathName.endsWith(".e"+parent.ext) )
			return;	// do nothing. This grammar is invalid.
		
		
		
		final String prefix = schemaFileName.substring(0, schemaFileName.lastIndexOf('.')+1);
		// gets examples.
		String[] lst = parent.testDir.list( new FilenameFilter (){ 
			public boolean accept( File dir, String name ) {
				return name.startsWith(prefix) && name.endsWith(".xml")
					&& name.indexOf(".v")>=0;
			}
		} );
		if( lst.length!=0 ) {
			String example = parent.dir + File.separatorChar + lst[0];
			
			System.out.println("test for "+pathName + " with " + example);
			assert( "generator for "+pathName,
				new Driver().run( new String[]{
					"-seed","0",
					"-n","100",
					"-quiet",
					"-example", example,
					"-validate",pathName,
					"NUL"/*throw output away*/}, System.out )==0 );
			assert( "generator for "+pathName,
				new Driver().run( new String[]{
					"-seed","0",
					"-n","100",
					"-quiet",
					"-error","1/100",
					"-example", example,
					"-validate",pathName,
					"NUL"/*throw output away*/}, System.out )==0 );
		} else {
			System.out.println("test for "+pathName + " *** skipped" );
		}

	}
}
