package com.sun.tranquilo.datatype.conformance;

class SimpleTestPattern implements TestPattern
{
	/** returns the number of test cases to be generated */
	public long totalCases() { return 2; }	// pattern itself or the empty
	
	/** restart generating test cases */
	public void reset() { idx=0; }
	
	/** get the current test case */
	public TestCase get()
	{
		switch(idx)
		{
		case 0:		return theCase;
		case 1:		return TestCase.theEmptyCase;
		default:	throw new IllegalStateException();
		}
	}
	
	/** generate next test case */
	public void next() { idx++; }
	
	public boolean hasMore() { return idx!=2; }
	
	/** the only one test case which this obejct has */
	private final TestCase theCase;
	private int idx=0;
	
	SimpleTestPattern( TestCase theCase )
	{
		this.theCase = theCase;
		reset();
	}
}