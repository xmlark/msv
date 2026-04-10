/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.conformance;

import com.sun.msv.datatype.xsd.TypeIncubator;
import org.relaxng.datatype.DatatypeException;

/**
 * test pattern that corresponds with one test case.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class SimpleTestPattern implements TestPattern
{
    /** returns the number of test cases to be generated */
    public long totalCases() { return 1; }    // pattern itself or the empty
    
    /** restart generating test cases */
    public void reset() { idx=0; }
    
    /** get the current test case */
    public String get( TypeIncubator incubator ) throws DatatypeException
    {
        switch(idx)
        {
        case 0:
            incubator.addFacet( facetName, facetValue, false, DummyContextProvider.theInstance );
            return answer;
        default:
            throw new Error();
        }
    }
    
    /** generate next test case */
    public void next() { idx++; }
    
    public boolean hasMore() { return idx!=1; }

    public boolean isExpectedFailure() { return idx==0 && expectedFailure; }

    public String getExpectedFailureReason() {
        if(idx==0 && expectedFailure) return expectedFailureReason;
        return null;
    }

    private final String facetName;
    private final String facetValue;
    private final String answer;
    private final boolean expectedFailure;
    private final String expectedFailureReason;
    private int idx=0;
    
    SimpleTestPattern( String facetName, String facetValue, String answer, boolean expectedFailure, String expectedFailureReason )
    {
        this.facetName    = facetName;
        this.facetValue    = facetValue;
        this.answer        = answer;
        this.expectedFailure = expectedFailure;
        this.expectedFailureReason = expectedFailureReason;
        reset();
    }
}
