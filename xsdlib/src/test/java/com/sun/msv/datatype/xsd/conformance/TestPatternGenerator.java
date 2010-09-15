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

import org.jdom.Element;

import java.util.List;

/**
 * parses XML representation of test pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TestPatternGenerator
{
    /**
     * parses test pattern specification and returns it.
     *
     * @param patternElement
     *        one of "combination","choice","facet".
     */
    public static TestPattern parse( Element patternElement )
        throws Exception
    {
        final String tagName = patternElement.getName();
        if( tagName.equals("combination") || tagName.equals("choice") )
        {
            // parse children
            List lst = patternElement.getChildren();
            TestPattern[] children = new TestPattern[lst.size()];
            for( int i=0; i<lst.size(); i++ )
                children[i] = parse( (Element)lst.get(i) );
            
            if( tagName.equals("combination") )
            {
                boolean mode = true;    // default is 'AND'
                if( patternElement.getAttribute("mode")!=null
                &&  patternElement.getAttributeValue("mode").equals("or") )
                    mode = false;
                
                return new FullCombinationPattern(children,mode);
            }
            else
            {
                return new ChoiceTestPattern(children);
            }
        }
        if( tagName.equals("facet") )
        {
            return new SimpleTestPattern(
                patternElement.getAttributeValue("name"),
                patternElement.getAttributeValue("value"),
                trimAnswer(patternElement.getAttributeValue("answer")) );
        }
        
        throw new Exception("unknown pattern:"+tagName);
    }
    
    public static String trimAnswer( String answer )
    {
        String r="";
        final int len = answer.length();
        for( int i=0; i<len; i++ )
        {
            final char ch = answer.charAt(i);
            if(ch=='o' || ch=='.')    r+=ch;
        }
        
        return r;
    }

    /** merges another test case into this */
    public static String merge( String a1, String a2, boolean mergeAnd )
    {
        if(a1==null)    return a2;
        if(a2==null)    return a1;
        if( a1.length()!=a2.length() )
            throw new Error("assertion: lengths of the answers are different");
        
        final int len = a1.length();
        String newAnswer ="";
        for( int i=0; i<len; i++ )
        {
            if( ( mergeAnd && (a1.charAt(i)=='o' && a2.charAt(i)=='o' ) )
            ||  (!mergeAnd && (a1.charAt(i)=='o' || a2.charAt(i)=='o' ) ) )
                newAnswer += "o";
            else
                newAnswer += ".";
        }
        
        return newAnswer;
    }
}
