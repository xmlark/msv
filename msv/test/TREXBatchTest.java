import junit.framework.*;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester.
 * 
 * for use by automated test by ant.
 */
public class TREXBatchTest
{
	public static TestSuite suite()
	{
		return
			new BatchVerifyTester(
				"trex",
				System.getProperty("TREXBatchTestDir"),
				".trex" ).suite();
	}
}
