package com.sun.tranquilo.datatype.test;

class TestPatternGenerator
{
	static final TestCase theEmptyCase = new TestCase();
	
	/** choose one from child pattern */
	static class ChoicePattern implements TestPattern
	{
		private final TestPattern[] children;
		private int idx=0;
		private TestCase theCase;
		
		public ChoicePattern( TestPattern[] children )
		{
			this.children = children;
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
				theCase = theEmptyCase;
				return;
			}
			if(idx==-2)
			{// indicates completion of the test
				idx=-3;
				return;
			}
			
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
			
			if( theCase==theEmptyCase )
				next();	// ignore empty pattern from children.
		}

		public boolean hasMore() { return idx!=-3; }
	}
	
	/** test every possible combination of child patterns */
	static class FullCoverageCombinationPattern implements TestPattern
	{
		private final TestPattern[] children;
		private TestCase theCase;
		private final boolean mergeMode;
		
		public FullCoverageCombinationPattern( TestPattern[] children, boolean mergeMode )
		{
			this.children = children;
			this.mergeMode = mergeMode;
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
			next();	// get the first one
		}

		public TestCase get() { return theCase; }
		
		/** generate next test case */
		public void next()
		{
			theCase = new TestCase();
			for( int i=0; i<children.length; i++ )
				theCase.merge( children[i].get(), mergeMode );
			
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
					return;
				else
					children[i].reset();
			}
			
			if(i==0)	return;	// test completed. no more pattern.
		}

		public boolean hasMore()
		{
			for( int i=0; i<children.length; i++ )
				if(children[i].hasMore())	return true;
			return false;
		}
	}
}
