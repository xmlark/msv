package com.sun.tranquilo.datatype.test;

import com.sun.tranquilo.datatype.BadTypeException;

/** test every possible combination of child patterns */
class FullCombinationPattern implements TestPattern
{
	private final TestPattern[] children;
	private TestCase theCase;
	/** True indicates 'AND' mode. False is 'OR' mode. */
	private final boolean mergeMode;
	
	private boolean noMore;

	public FullCombinationPattern( TestPattern[] children, boolean mergeMode )
	{
		this.children = children;
		this.mergeMode = mergeMode;
		reset();
	}

	/** returns the number of test cases to be generated */
	public long totalCases()
	{
		// to enumerate every possible combination
		// of every possible underlying patterns,
		// we have to need l times m times n ... where
		// l,m,n are numbers of child test cases.
		long result = 1;
		for( int i=0; i<children.length; i++ )
			result *= children[i].totalCases();
		// every test pattern comes with empty facet, and
		// as a result this calculation includes empty case,
		// without any further hussle.
		return result;
	}

	/** restart generating test cases */
	public void reset()
	{
		for( int i=0; i<children.length; i++ )
			children[i].reset();
		noMore=false;
		next();	// get the first one
	}

	public TestCase get() { return theCase; }

	/** generate next test case */
	public void next()
	{
		// debug
		if(!hasMore())	throw new IllegalStateException();
			
		theCase = new TestCase();
		try
		{
			for( int i=0; i<children.length; i++ )
				theCase.merge( children[i].get(), mergeMode );
		}
		catch(BadTypeException bte) { throw new RuntimeException(bte.getMessage()); }

		// increment.
		// Imagine a increment of number.
		// 09999 + 1 => 10000
		// so if an increment results in carry, reset the digit
		// and increment the next digit.
		// this's what is done here.
		int i;
		for( i=children.length-1; i>=0; i-- )
		{
			children[i].next();
			if( children[i].hasMore() )
			{
				for( i++; i<children.length; i++ )
					children[i].reset();
				return;
			}
		}

		if(i==-1)	noMore=true;	// test completed. no more pattern.
	}

	public boolean hasMore()
	{
		return !noMore;
	}
}
