/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.datetime;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sun.msv.datatype.xsd.Comparator;
import com.sun.msv.datatype.xsd.DateTimeType;
import com.sun.msv.datatype.xsd.DateType;
import com.sun.msv.datatype.xsd.DurationType;
import com.sun.msv.datatype.xsd.GYearMonthType;

/**
 * tests BigDateTimeValueType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BigDateTimeValueTypeTest extends TestCase {    
    
    public BigDateTimeValueTypeTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(BigDateTimeValueTypeTest.class);
    }
    
    /** Test of getBigValue method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testGetBigValue()  throws Exception
    {
        BigDateTimeValueType t = parseYearMonth("2000-01");
        assertEquals( t, t.getBigValue() );
    }
    
    private BigDateTimeValueType parseYearMonth(String s) {
        return (BigDateTimeValueType)
            GYearMonthType.theInstance.createValue(s,null);
    }

    private BigDateTimeValueType parseDateTime( String s ) {
        return (BigDateTimeValueType)DateTimeType.theInstance.createValue(s,null);
    }

    private BigDateTimeValueType parseDate( String s ) {
        return (BigDateTimeValueType)DateType.theInstance.createValue(s,null);
    }
    
    private BigTimeDurationValueType parseDuration( String s ) {
        return (BigTimeDurationValueType)DurationType.theInstance.createValue(s,null);
    }
    
    /** Test of compare method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testCompare() throws Exception
    {
        // from examples of the spec
        int r;
        
        r = parseDateTime("2000-01-15T00:00:00").compare(
            parseDateTime("2000-02-15T00:00:00") );
        assertEquals( r, Comparator.LESS );
            
        r = parseDateTime("2000-01-15T12:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.LESS );
            
        r = parseDateTime("2000-01-01T12:00:00" ).compare(
            parseDateTime("1999-12-31T23:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
        
        r = parseDateTime("2000-01-16T12:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
            
        r = parseDateTime("2000-01-16T00:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
    }
    
    /** Test of normalize method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testNormalize() throws Exception
    {
        IDateTimeValueType v;
        
        v = parseDateTime("2000-03-04T23:00:00-03:00").normalize();
        
        // equals method compares two by calling normalize,
        // so actually this cannot be said as a testing.
        assertEquals( v, parseDateTime("2000-03-05T02:00:00Z") );
    }
    
    /** Test of add method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testAdd() throws Exception
    {
        BigDateTimeValueType v;
        
        // from examples of Appendix.E of the spec.
        
        v = parseDateTime("2000-01-12T12:13:14Z").add(
                parseDuration("P1Y3M5DT7H10M3.3S") ).getBigValue();
        assertEquals( v, parseDateTime("2001-04-17T19:23:17.3Z") );
        
        v = parseYearMonth("2000-01").add( parseDuration("-P3M") ).getBigValue();
        assertEquals( v, parseYearMonth("1999-10") );
        
        v = parseDate("2000-01-12-05:00").add(
                parseDuration("PT33H") ).getBigValue();
        assertEquals( v, parseDate("2000-01-13-05:00") );
    }
    
}
