import junit.framework.*;
import java.util.StringTokenizer;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 */
public class RELAXBatchTest
{
	public static TestSuite suite()
	{
		StringTokenizer tokens = new StringTokenizer( System.getProperty("RELAXBatchTestDir"), ";" );
		
		TestSuite s = new TestSuite();
		while( tokens.hasMoreTokens() )
			s.addTest(
				new BatchVerifyTester("relax", tokens.nextToken(), ".rlx").suite() );
		
		return s;
	}
}
