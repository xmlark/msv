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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.InputSource;
import org.relaxng.testharness.model.*;
import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.reader.TestSuiteReader;

/**
 * creates a JUnit TestSuite from the specified RELAX NG test suite.
 * 
 * <p>
 * By deriving this class, you can "filter" the test cases.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestSuiteBuilder implements TestVisitor {

	public TestSuiteBuilder( IValidator validator ) {
		this.validator = validator;
	}
	
	protected final IValidator validator;
	
	/**
	 * parses the specified source as a RELAX NG test suite file,
	 * and returns a JUnit TestSuite that runs all the tests contained
	 * in it.
	 */
	public TestSuite create( InputSource source ) throws Exception {
		
		return create( TestSuiteReader.parse(source) );
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG Test Suite.
	 */
	public TestSuite create( RNGTestSuite suite ) {
		TestSuite jsuite = new TestSuite();
		
		RNGTest[] tests = suite.getAllTests();
		
		for( int i=0; i<tests.length; i++ )
			jsuite.addTest( (Test)tests[i].visit(this) );
		
		return jsuite;
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG test case.
	 */
	public TestSuite create( final RNGValidTestCase testCase ) {
		TestSuite jsuite = new TestSuite();
		
		final ISchema[] schema = new ISchema[1];
		
		String caseTitle = testCase.header.getTitle();
		if(caseTitle==null)	caseTitle="";
		
		// parse the schema first
		jsuite.addTest( new TestCase( caseTitle ){
			public void runTest() throws Exception {
				// load schema
				schema[0] = validator.parseSchema( testCase.pattern );
				assert( "validator fails to compile a valid pattern", schema[0]!=null );
			}
		});
		
		for( int i=0; i<testCase.validDocuments.length; i++ ) {
			final XMLDocument instance = testCase.validDocuments[i];
			jsuite.addTest( new TestCase( caseTitle+":v"+i ){
				public void runTest() throws Exception {
					// if the validator failed to parse the schema,
					// skip this test.
					if( schema[0]==null)	return;
					assert(
						"validator rejects a valid document",
						validator.validate( schema[0], instance ) );
				}
			});
		}
		
		for( int i=0; i<testCase.invalidDocuments.length; i++ ) {
			final XMLDocument instance = testCase.invalidDocuments[i];
			jsuite.addTest( new TestCase( caseTitle+":n"+i ){
				public void runTest() throws Exception {
					// if the validator failed to parse the schema,
					// skip this test.
					if( schema[0]==null)	return;
					assert(
						"validator accepts an invalid document",
						!validator.validate( schema[0], instance ) );
				}
			});
		}
		
		return jsuite;
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG test case.
	 */
	public TestSuite create( final RNGInvalidTestCase testCase ) {
		TestSuite jsuite = new TestSuite();

		String caseTitle = testCase.header.getTitle();
		if(caseTitle==null)	caseTitle="";
		
		for( int i=0; i<testCase.patterns.length; i++ ) {
			final XMLDocument instance = testCase.patterns[i];
			jsuite.addTest( new TestCase( caseTitle+":"+i ){
				public void runTest() throws Exception {
					// load schema
					ISchema schema = validator.parseSchema( instance );
					assert( "validator didn't reject an invalid pattern", schema==null );
				}
			});
		}
		
		return jsuite;
	}
	
//
// TestVisitor implementation
//
	public Object onValidTest( RNGValidTestCase test ) {
		return create(test);
	}
	public Object onInvalidTest( RNGInvalidTestCase test ) {
		return create(test);
	}
	public Object onSuite( RNGTestSuite suite ) {
		return create(suite);
	}

}
