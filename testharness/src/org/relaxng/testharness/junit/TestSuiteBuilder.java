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
import java.util.Iterator;
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
		String title = null;
		if( suite.header!=null ) title = suite.header.getName();
		if(title==null)	title="";
		
		TestSuite jsuite = new TestSuite(title);
		
		RNGTest[] tests = suite.getAllTests();
		
		for( int i=0; i<tests.length; i++ )
			jsuite.addTest( (Test)tests[i].visit(this) );
		
		return jsuite;
	}
	
	private String getName( RNGHeader header, String defaultValue ) {
		if(header==null)	return defaultValue;
		String value = header.getName();
		if(value!=null)		return value;
		else				return defaultValue;
	}
	
	/**
	 * returns a JUnit TestSuite that runs all the tests contained
	 * in the specified RELAX NG test case.
	 */
	public TestSuite create( final RNGValidTestCase testCase ) {
		TestSuite jsuite = new TestSuite();
		
		final ISchema[] schema = new ISchema[1];
		
		String caseTitle = getName(testCase.header,"no-name");
		
		// parse the schema first
		jsuite.addTest( new TestCase( caseTitle ){
			public void runTest() throws Exception {
				// load schema
				schema[0] = validator.parseSchema( testCase.pattern, testCase.header );
				assert( "validator fails to compile a valid pattern", schema[0]!=null );
				
				checkCompatibility(
					schema[0].isAnnotationCompatible(),
					testCase.isAnnotationCompatible,
					"annotation");
				checkCompatibility(
					schema[0].isDefaultValueCompatible(),
					testCase.isDefaultValueCompatible,
					"default value");
				checkCompatibility(
					schema[0].isIdIdrefCompatible(),
					testCase.isIdIdrefCompatible,
					"ID/IDREF");
			}
			
			private void checkCompatibility( Boolean result, boolean expected, String msg ) {
				if(result==null)
					return;	// this implementation is not a compatibility processor.
				if(result.booleanValue()!=expected) {
					// error. different from the expected result.
					if(expected==true)
						fail("the schema parser fails to recognize a "+msg+" compatible schema");
					else
						fail("the schema parser fails to detect a "+msg+" incompatible schema");
				}
			}
		});
		
		Iterator itr = testCase.iterateValidDocuments();
		int i=0;
		while( itr.hasNext() ) {
			final ValidDocument instance = (ValidDocument)itr.next();
			jsuite.addTest( new TestCase(
				getName(instance.document.getHeader(), caseTitle+":v"+(i++)) ){
				public void runTest() throws Exception {
					// if the validator failed to parse the schema,
					// skip this test.
					if( schema[0]==null)	return;
					
					boolean result = validator.validate( schema[0], instance.document );
					
					// TODO: this is a quick hack. fix it.
					boolean expected = instance.isIdIdrefSound;
					
					if(result!=expected) {
						if(expected==true)
							fail("validator rejects a valid document");
						else
							fail("validator accepts an invalid document");
					}
				}
			});
		}
		
		itr = testCase.iterateInvalidDocuments();
		i=0;
		while( itr.hasNext() ) {
			final XMLDocument instance = (XMLDocument)itr.next();
			jsuite.addTest( new TestCase(
				getName(instance.getHeader(), caseTitle+":n"+(i++)) ){
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

		String caseTitle = testCase.header.getName();
		if(caseTitle==null)	caseTitle="";
		
		Iterator itr = testCase.iteratePatterns();
		int i=0;
		while( itr.hasNext() ) {
			final XMLDocument instance = (XMLDocument)itr.next();
			jsuite.addTest( new TestCase( caseTitle+":"+(i++) ){
				public void runTest() throws Exception {
					// load schema
					ISchema schema = validator.parseSchema( instance, testCase.header );
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
