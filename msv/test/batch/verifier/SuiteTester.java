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
import java.io.File;
import org.relaxng.testharness.junit.TestSuiteBuilder;
import org.relaxng.testharness.validator.IValidator;
import org.relaxng.testharness.reader.TestSuiteReader;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * incorporates .ssuite files into JUnit test suite.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SuiteTester {
	
	// where to load an impl.
	private final TestSuiteBuilder builder;
	
	public SuiteTester( IValidator validator ) {
		builder = new TestSuiteBuilder(validator);
	}
	
	/**
	 * creates a JUnit TestSuite that tests all .ssuite files in the
	 * given directory.
	 */
	public TestSuite createTestSuiteFromDir( File testDir ) throws Exception {
		return createTestSuiteFromDir( testDir, ".ssuite" );
	}
	
	public TestSuite createTestSuiteFromDir( File testDir, final String extension ) throws Exception {
		// collect suite files.
		
		String[] suiteFiles = testDir.list( new FilenameFilter(){
			public boolean accept( File dir, String name ) {
				return name.endsWith(extension);
			}
		} );
		
		TestSuite suite = new TestSuite();

		if( suiteFiles==null )	return suite;

		for( int i=0; i<suiteFiles.length; i++ )
			suite.addTest( createTestSuiteFromFile(
				new File( testDir, suiteFiles[i] ) ) );
		
		return suite;
	}
	
	/**
	 * creates a JUnit TestSuite that tests given suite file.
	 */
	public TestSuite createTestSuiteFromFile( File suiteFile ) throws Exception {
		// there is a bug in Xerces DOM implementation
		// that causees "DOM03 namespace error". Use crimson to avoid this problem.
		// Ideally, this method should be
		// return builder.create(
		//		new InputSource( new FileInputStream(suiteFile) ) );
		
		javax.xml.parsers.DocumentBuilderFactory factory = new org.apache.crimson.jaxp.DocumentBuilderFactoryImpl();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		
		return builder.create(
			TestSuiteReader.parse(
				new InputSource( new FileInputStream(suiteFile) ), factory ) );
	}
	
	/**
	 * driver.
	 */
	public static void main( String[] args ) throws Exception {
		
		if( args.length<2 ) {
			System.out.println(
				"Usage: SuiteTester (xsd|rng) <suite file or directory> ...\n"+
				"\n"+
				"test all test cases in the specified suites\n"+
				"If a dir is specified, all .ssuite files in the dir is parsed.\n");
			return;
		}
		
		IValidator val =null;
		if( args[0].equalsIgnoreCase("xsd") )
			val = new msv.IValidatorImplForXS();
		if( args[0].equalsIgnoreCase("rng") )
			val = new msv.IValidatorImplForRNG();
		
		if( val==null ) {
			System.out.println("unrecognized schema type: "+args[0]);
			return;
		}
		
		// collect test cases
		TestSuite jsuite = new TestSuite();
		SuiteTester tester = new SuiteTester(val);
		
		for( int i=1; i<args.length; i++ ) {
			File f = new File(args[i]);
			if(f.isFile())
				jsuite.addTest(tester.createTestSuiteFromFile(f));
			else
				jsuite.addTest(tester.createTestSuiteFromDir(f));
		}
		
		// run the test
		junit.textui.TestRunner.run(jsuite);
	}
}
