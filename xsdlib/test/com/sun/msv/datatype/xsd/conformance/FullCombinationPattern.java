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

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.datatype.xsd.TypeIncubator;

/**
 * test every possible combination of child patterns.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class FullCombinationPattern implements TestPattern
{
    private final TestPattern[] children;
    /** True indicates 'AND' mode. False is 'OR' mode. */
    private final boolean mergeMode;
    
    private boolean noMore = false;
    
    public FullCombinationPattern( TestPattern[] children, boolean mergeMode )
    {
        for( int i=0; i<children.length; i++ )
            children[i] = new PatternWithEmpty(children[i]);
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
    }

    public String get( TypeIncubator ti ) throws DatatypeException
    {
        String answer=null;
        for( int i=0; i<children.length; i++ )
            answer = TestPatternGenerator.merge( answer, children[i].get(ti), mergeMode );
        
        return answer;
    }

    /** generate next test case */
    public void next()
    {
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
        
        noMore = true;
    }

    public boolean hasMore()
    {
        return !noMore;
    }
    
    /**
     * adds empty test case to the base pattern
     */
    private static class PatternWithEmpty implements TestPattern
    {
        private final TestPattern base;
        private int mode;
        
        PatternWithEmpty( TestPattern base ) { this.base=base; reset(); }
        
        public long totalCases() { return base.totalCases()+1; }
    
        public void reset() { base.reset();mode=0; }
    
        public String get( TypeIncubator ti ) throws DatatypeException
        {
            switch(mode)
            {
            case 0:        return base.get(ti);
            case 1:        return null;
            default:    throw new Error();
            }
        }
    
        public void next()
        {
            switch(mode)
            {
            case 0:
                base.next();
                if(!base.hasMore())        mode=1;
                return;
            case 1:
                mode=2; return;
            default:
                throw new Error();
            }
        }
    
        public boolean hasMore()    { return mode!=2; }
    }
}
