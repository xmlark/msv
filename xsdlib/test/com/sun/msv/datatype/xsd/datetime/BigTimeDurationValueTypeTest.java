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

import java.math.BigInteger;
import java.math.BigDecimal;
import com.sun.msv.datatype.xsd.Comparator;
import junit.framework.*;
import java.io.ByteArrayInputStream;

/**
 * tests BigTimeDurationValueType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BigTimeDurationValueTypeTest extends TestCase {    
	
	public BigTimeDurationValueTypeTest(String testName) {
		super(testName);
	}
	
	public static void main(String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(BigTimeDurationValueTypeTest.class);
	}
	
	private ITimeDurationValueType get( String s ) throws Exception
	{
		return new ISO8601Parser(new ByteArrayInputStream(s.getBytes("UTF-8"))).durationTypeV();
	}
	
	/** Test of hashCode method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
	public void testHashCode() throws Exception
	{
		assertEquals( get("P400Y").hashCode(), get("P146097D").hashCode() );
		assertEquals( get("P1D").hashCode(), get("PT24H").hashCode() );
	}
	
	/** Test of compare method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
	public void testCompare() throws Exception
	{
		assertEquals( get("P1D").compare( get("PT24H") ), Comparator.EQUAL );

		assertEquals( get("P1Y").compare( get("P364D") ), Comparator.GREATER );
		assertEquals( get("P1Y").compare( get("P365D") ), Comparator.GREATER );
		assertEquals( get("P1Y").compare( get("P366D") ), Comparator.LESS );
		assertEquals( get("P1Y").compare( get("P367D") ), Comparator.LESS );

		assertEquals( get("P1M").compare( get("P27D") ), Comparator.GREATER );
		assertEquals( get("P1M").compare( get("P28D") ), Comparator.GREATER );
		assertEquals( get("P1M").compare( get("P29D") ), Comparator.UNDECIDABLE );
		assertEquals( get("P1M").compare( get("P30D") ), Comparator.UNDECIDABLE );
		assertEquals( get("P1M").compare( get("P31D") ), Comparator.LESS );
		assertEquals( get("P1M").compare( get("P32D") ), Comparator.LESS );
		
		assertEquals( get("P5M").compare( get("P149D") ), Comparator.GREATER );
		assertEquals( get("P5M").compare( get("P150D") ), Comparator.GREATER );
		assertEquals( get("P5M").compare( get("P151D") ), Comparator.UNDECIDABLE );
		assertEquals( get("P5M").compare( get("P152D") ), Comparator.UNDECIDABLE );
		assertEquals( get("P5M").compare( get("P153D") ), Comparator.LESS );
		assertEquals( get("P5M").compare( get("P154D") ), Comparator.LESS );
		
		assertEquals( get("P400Y").compare( get("P146097D") ), Comparator.EQUAL );
	}
	
	/** Test of getBigValue method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
	public void testGetBigValue() throws Exception
	{
		ITimeDurationValueType td = get("P153D");
		assertSame( td.getBigValue(), td );
	}
	
//	/** Test of fromMinutes method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
///	public void testFromMinutes()
}
