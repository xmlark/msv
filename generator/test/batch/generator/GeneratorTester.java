package batch.generator;

import batch.model.TestReader;

/**
 * command line driver. Useful for hand-debugging.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GeneratorTester extends batch.BatchTester {

    protected TestReader createReader() {
        try {
            return new TestReader( new TestBuilderImpl(validator,factory) );
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }
    
	public static void main( String[] av ) throws Exception {
        new GeneratorTester().run(av);
	}
	
	protected void usage() {
		System.out.println(
			"usage "+this.getClass().getName()+" (relax|trex|xsd|dtd|rng) [-strict] <test case directory>\n"+
			"  tests the generator by using schema files and test instances\n"+
			"  in the specified directory.");
	}
}
