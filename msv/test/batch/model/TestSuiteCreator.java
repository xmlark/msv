package batch.model;

import org.relaxng.testharness.model.*;
import junit.framework.*;
import java.util.Iterator;

/**
 * Traverses all test suites recursively and creates
 * JUnit {@link TestSuite}.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestSuiteCreator implements TestVisitor
{
	public Object onValidTest( RNGValidTestCase tcase ) {
		return new TestSuite();
	}
	public Object onInvalidTest( RNGInvalidTestCase tcase ) {
		return new TestSuite();
	}
	public Object onSuite( RNGTestSuite src ) {
		TestSuite suite = new TestSuite();
				
		Iterator itr = src.iterateTests();
		while(itr.hasNext()) {
			RNGTest test = (RNGTest)itr.next();
			suite.addTest( (Test)test.visit(this) );
		}
				
		return suite;
	}
}
