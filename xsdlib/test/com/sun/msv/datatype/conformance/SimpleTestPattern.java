/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype.conformance;

import com.sun.tranquilo.datatype.*;

class SimpleTestPattern implements TestPattern
{
	/** returns the number of test cases to be generated */
	public long totalCases() { return 1; }	// pattern itself or the empty
	
	/** restart generating test cases */
	public void reset() { idx=0; }
	
	/** get the current test case */
	public String get( TypeIncubator incubator ) throws BadTypeException
	{
		switch(idx)
		{
		case 0:
			incubator.add( facetName, facetValue, false, DummyContextProvider.theInstance );
			return answer;
		default:
			throw new Error();
		}
	}
	
	/** generate next test case */
	public void next() { idx++; }
	
	public boolean hasMore() { return idx!=1; }

	private final String facetName;
	private final String facetValue;
	private final String answer;
	private int idx=0;
	
	SimpleTestPattern( String facetName, String facetValue, String answer )
	{
		this.facetName	= facetName;
		this.facetValue	= facetValue;
		this.answer		= answer;
		reset();
	}
}