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

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.xml.sax.InputSource;
import org.relaxng.testharness.model.*;
import org.relaxng.testharness.validator.*;
import org.relaxng.testharness.reader.TestSuiteReader;

/**
 * creates a JUnit TestSuite from the specified RELAX NG test suite.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestSuiteBuilder
{
	/**
	 * parses the specified source as a RELAX NG test suite file,
	 * and returns a JUnit TestSuite that runs all the tests contained
	 * in it.
	 */
	public static TestSuite create( InputSource source, IValidator validator ) throws Exception {
		
		return create( TestSuiteReader.parse(source), validator );
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG Test Suite.
	 */
	public static TestSuite create( RNGTestSuite suite, IValidator validator ) {
		TestSuite jsuite = new TestSuite();
		
		for( int i=0; i<suite.validTestCases.length; i++ )
			jsuite.addTest( create( suite.validTestCases[i], validator ) );
		
		for( int i=0; i<suite.invalidTestCases.length; i++ )
			jsuite.addTest( create( suite.invalidTestCases[i], validator ) );
		
		return jsuite;
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG test case.
	 */
	public static TestSuite create( final RNGValidTestCase testCase, final IValidator validator ) {
		TestSuite jsuite = new TestSuite();
		
		final ISchema[] schema = new ISchema[1];
		
		// TODO: specify the test name
		
		// parse the schema first
		jsuite.addTest( new TestCase( testCase.pattern.getTitle() ){
			public void runTest() throws Exception {
				// load schema
				schema[0] = validator.parseSchema( testCase.pattern );
				assert( "validator fails to compile a valid pattern", schema[0]!=null );
			}
		});
		
		for( int i=0; i<testCase.validDocuments.length; i++ ) {
			final XMLDocument instance = testCase.validDocuments[i];
			jsuite.addTest( new TestCase( instance.getTitle() ){
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
			jsuite.addTest( new TestCase( instance.getTitle() ){
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
	public static TestSuite create( final RNGInvalidTestCase testCase, final IValidator validator ) {
		TestSuite jsuite = new TestSuite();
		
		for( int i=0; i<testCase.patterns.length; i++ ) {
			final XMLDocument instance = testCase.patterns[i];
			jsuite.addTest( new TestCase( instance.getTitle() ){
				public void runTest() throws Exception {
					// load schema
					ISchema schema = validator.parseSchema( instance );
					assert( "validator didn't reject an invalid pattern", schema==null );
				}
			});
		}
		
		return jsuite;
	}
}
