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

import batch.*;
import batch.model.*;
import junit.framework.*;
import java.util.*;
import org.relaxng.testharness.model.*;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.writer.*;

/**
 * tests converter by using JUnit.
 * 
 * <p>
 * This class first loads the grammar, then converts it to a specific
 * language by using a GrammarWriter. Next, GrammarReader is used to
 * parse the converted grammar. Finally, test instances are validated
 * against re-parsed Grammar to make sure that the conversion was in fact
 * successful.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BatchWriterTester extends batch.BatchTester {

	public TestSuite suite( RNGTestSuite source ) {
		
		return (TestSuite)source.visit( new TestSuiteCreator(){
			public Object onValidTest( RNGValidTestCase tcase ) {
				if("yes".equals(tcase.header.getProperty("","skipConverter")))
					return new TestSuite();	// skip this test case
				
				return createTestCase(tcase);
			}
		});
	}

	
	
	/*
		RELAX allows undeclared attributes, but no other schema language does.
		Therefore, some RELAX test instances which are originally valid can
		results in an invalid instance.
	
		this set contains all such test cases.
	*/
	protected static Set invalidTestCases;
	static {
		Set s = new java.util.HashSet();
		s.add("relax001.v00.xml");
		s.add("relax031.v00.xml");
		s.add("relax039.v00.xml");
		s.add("relax040.v00.xml");
		s.add("relax041.v00.xml");
		invalidTestCases = s;
	}
	
	
	/**
	 * Creates a test case from a given RNGValidTestCase.
	 * 
	 * A test consists of
	 * 
	 * 1. converts a grammar into the target format
	 * 2. tests the instance documents.
	 */
	protected Test createTestCase( final RNGValidTestCase test ) {
		
		TestSuite suite = new TestSuite();
		
		final Grammar[] grammar = new Grammar[1];
		
		
		suite.addTest( new TestCase( getName(test.header) + ": schema conversion") {
			public void runTest() throws Exception {
				// load grammar
				Grammar g = validator.parseSchema(
					test.pattern.getAsInputSource(),
					new ThrowErrorController(resolver) );
				if( g==null )
					fail("failed to parse the original grammar");	// unexpected result
		
				// then convert it to the target grammar,
				// and parse it by the target grammar reader parser.
				GrammarWriter writer = getWriter();
				GrammarReader reader = createReader();
				
				writer.setDocumentHandler(
					new ContentHandlerAdaptor(reader));
				writer.write(g);
		
				grammar[0] = reader.getResultAsGrammar();
				if( grammar[0]==null )
					fail("conversion failed");	// unexpected result
			}
		});
		
		Iterator itr;
		
		// test valid instances
		itr = test.iterateValidDocuments();
		while( itr.hasNext() ) {
			final ValidDocument document = (ValidDocument)itr.next();
			
			if(document.document.getHeader()!=null
			&& invalidTestCases.contains(document.document.getHeader().getProperty("","fileName")) )
				continue;	// this should be skipped
				
			suite.addTest( new TestCase(
				getName(test.header)+"/"+getName(document.document.getHeader()) ){
				public void runTest() throws Exception {
					assert(
						"validator fails to accept a valid document",
						validator.validate( grammar[0], document.document )
					);
				}
			});
		}
		
		// test invalid instances
		itr = test.iterateInvalidDocuments();
		while( itr.hasNext() ) {
			final XMLDocument document = (XMLDocument)itr.next();
			
			suite.addTest( new TestCase(
				getName(test.header)+"/"+getName(document.getHeader()) ){
				public void runTest() throws Exception {
					assert(
						"validator accepts an invalid document",
						!validator.validate( grammar[0], document )
					);
				}
			});
		}
		
		return suite;
	}
	
	protected abstract GrammarReader createReader();
	protected abstract GrammarWriter getWriter();
}
