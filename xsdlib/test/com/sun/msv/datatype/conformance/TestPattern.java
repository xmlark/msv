package com.sun.tranquilo.datatype.conformance;

public interface TestPattern
{
	/** returns the number of test cases to be generated */
	long totalCases();
	
	/** restart generating test cases */
	void reset();
	
	/** get the current test case */
	TestCase get();
	
	/** generate next test case */
	void next();
	
	/** true indicates get method can be safely called */
	boolean hasMore();
}