package com.sun.tranquilo.datatype.conformance;

/** choose one from child pattern */
class ChoiceTestPattern implements TestPattern
{
	private final TestPattern[] children;
	private int idx=0;
	private TestCase theCase;

	public ChoiceTestPattern( TestPattern[] children )
	{
		this.children = children;
		reset();
	}

	/** returns the number of test cases to be generated */
	public long totalCases()
	{
		int result=1;
		for( int i=0; i<children.length; i++ )
			// every pattern comes with empty. So we have to remove it.
			result += children[i].totalCases()-1;
		return result;
	}

	/** restart generating test cases */
	public void reset()
	{
		for( int i=0; i<children.length; i++ )
			children[i].reset();
		idx=0;
		next();	// fetch the first one
	}

	/** get the current test case */
	public TestCase get() { return theCase; }

	/** generate next test case */
	public void next()
	{
		if(idx==-1)
		{
			idx=-2;
			theCase = TestCase.theEmptyCase;
			return;
		}
		if(idx==-2)
		{// indicates completion of the test
			idx=-3;
			return;
		}
		if(idx==-3)
			throw new IllegalStateException("illegal call to next");

		int prev = idx;

		theCase = children[idx].get();
		children[idx].next();

		do
		{
			idx = (idx+1)%children.length;
		}while(!children[idx].hasMore() && idx!=prev);

		if(!children[idx].hasMore())
		{// all patterns of children have enumerated
			idx=-1;	// next time, we will return empty test pattern
		}

		if( theCase==TestCase.theEmptyCase )
			next();	// ignore empty pattern from children.
	}

	public boolean hasMore() { return idx!=-3; }
}
