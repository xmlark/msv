import junit.framework.*;
import java.util.StringTokenizer;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 */
public class TREXBatchTest
{
	public static TestSuite suite()
	{
		StringTokenizer tokens = new StringTokenizer( System.getProperty("TREXBatchTestDir"), ";" );
		
		TestSuite s = new TestSuite();
		while( tokens.hasMoreTokens() )
			s.addTest(
				new BatchVerifyTester("trex", tokens.nextToken(), ".trex").suite() );
		
		return s;
	}
}
