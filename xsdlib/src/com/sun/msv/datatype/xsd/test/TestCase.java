package com.sun.tranquilo.datatype.test;

import com.sun.tranquilo.datatype.Facets;

class TestCase
{
	public final Facets	facets;
	public String	answer;
	
	public TestCase( Facets facets, String answer )
	{
		this.facets = facets;
		this.answer = answer;
	}
	
	/** creates an empty TestCase */
	public TestCase()
	{ this( new Facets(), null ); }
	
	/** merges another test case into this */
	public void merge( TestCase rhs, boolean mergeAnd )
	{
		if( answer==null )
		{// mine is empty. So copy it from rhs
			answer = rhs.answer;
			facets.merge(rhs.facets);
			return;
		}
		if( rhs.answer==null )
			return;	// rhs is empty. do nothing
		
		// both is not empty. merge it.
		facets.merge( rhs.facets );
		
		if( answer.length()!=rhs.answer.length() )
			throw new IllegalStateException("assertion: lengths of the answers are different");
		
		final int len = answer.length();
		String newAnswer ="";
		for( int i=0; i<len; i++ )
		{
			if( ( mergeAnd && (answer.charAt(i)=='o' && rhs.answer.charAt(i)=='o' ) )
			||  (!mergeAnd && (answer.charAt(i)=='o' || rhs.answer.charAt(i)=='o' ) ) )
				newAnswer += "o";
			else
				newAnswer += ".";
		}
		
		answer = newAnswer;
	}
}