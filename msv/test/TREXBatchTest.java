import junit.framework.*;

/**
 * tests the entire RELAX test suite by using BatchVerifyTester 
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
