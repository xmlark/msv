package batch.generator;

import junit.framework.*;
import batch.model.TestSuiteCreator;
import org.relaxng.testharness.model.*;
import msv.ISchemaImpl;
import java.util.Iterator;
import com.sun.msv.generator.*;

/**
 * command line driver. Useful for hand-debugging.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GeneratorTester extends batch.BatchTester {

	public static void main( String[] av ) throws Exception {
		new GeneratorTester().run(av);
	}
	
	protected void usage() {
		System.out.println(
			"usage "+this.getClass().getName()+" (relax|trex|xsd|dtd|rng) [-strict] <test case directory>\n"+
			"  tests the generator by using schema files and test instances\n"+
			"  in the specified directory.");
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
		final String name = getName(test.header);
		return new TestCase(name){
			public void runTest() throws Exception {
				
				Iterator itr = test.iterateValidDocuments();
				if(!itr.hasNext()) {
					// this schema has no test instance.
					System.out.println("test for "+name+" *** skipped" );
					return;
				}
				
				
				Driver driver = new Driver();	// generator instance.
				
				// parse parameters
				driver.parseArguments(new String[]{"-seed","0", "-n","30", "-quiet"});
				
				// parse example documents
				while( itr.hasNext() ) {
					ValidDocument example = (ValidDocument)itr.next();
					example.document.getAsSAX( new ExampleReader(driver.exampleTokens) );
				}
				
				// set the grammar
				ISchemaImpl schema = (ISchemaImpl)validator.parseSchema( test.pattern, test.header );
				assert( "failed to parse the schema", schema!=null );
				driver.grammar = schema.grammar;
				driver.outputName = "NUL";
				
				// run the test
				assert( "generator for "+name, driver.run(System.out)==0 );
				
				
				// parse additional parameter
				// generally, calling the parseArguments method more than once
				// is not supported.
				driver.parseArguments(new String[]{"-error","10/100"});

				assert( "generator for "+name, driver.run(System.out)==0 );
			}
		};
	}
	

	public TestSuite suite( RNGTestSuite source ) {
		
		return (TestSuite)source.visit( new TestSuiteCreator(){
			public Object onValidTest( RNGValidTestCase tcase ) {
				if("yes".equals(tcase.header.getProperty("","skipGenerator")))
					return new TestSuite();	// skip this test case
				
				return createTestCase(tcase);
			}
		});
	}
}
