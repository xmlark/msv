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

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests ISO8601Parser.
 * 
 * please explicitly test for every production rule whether empty string is allowed.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ISO8601ParserTest extends TestCase {    
	
	public ISO8601ParserTest(java.lang.String testName) {
		super(testName);
	}
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(ISO8601ParserTest.class);
	}
	
	public static ISO8601Parser getParser( String content ) throws Exception
	{
		return new ISO8601Parser( new ByteArrayInputStream( content.getBytes("UTF8") ) );
	}
	
	interface LV
	{
		void L( ISO8601Parser p ) throws Exception;
		void V( ISO8601Parser p ) throws Exception;
	}
	
	/** generic test runner */
	public static void run( String[] allowed, String[] prohibited, LV test )
		throws Exception
	{
		for( int i=0; i<allowed.length; i++ )
		{
			test.L(getParser(allowed[i]));
			test.V(getParser(allowed[i]));
		}
		for( int i=0; i<prohibited.length; i++ )
		{
			try
			{
				test.L(getParser(prohibited[i]));
				fail();
			} catch( Throwable e ) {;}
			try
			{
				test.V(getParser(prohibited[i]));
				fail();
			} catch( Throwable e ) {;}
		}
	}
	
	public void testSecond() throws Exception
	{
		run(
			new String[]{"52","00.00000009"},
			new String[]{"5",".192",""},
			new LV(){
			 	public void L( ISO8601Parser p ) throws Exception { p.secondL(); }
			 	public void V( ISO8601Parser p ) throws Exception { p.secondV(); }
			 } );
	}
	
	public void testDatePart() throws Exception
	{
		run(
			new String[]{"5Y","600M","9250D"},
			new String[]{"","Y","5D6M"},
			new LV(){
			 	public void L( ISO8601Parser p ) throws Exception { p.datePartL(); }
			 	public void V( ISO8601Parser p ) throws Exception { p.datePartV(true); }
			 } );
	}
	
	public void testTimePart() throws Exception
	{
		run(
			new String[]{"5H","600M","9250S","1235.22S","5H2S"},
			new String[]{"","Y","5S6M"},
			new LV(){
			 	public void L( ISO8601Parser p ) throws Exception { p.timePartL(); }
			 	public void V( ISO8601Parser p ) throws Exception { p.timePartV(true); }
			 } );
	}
	
	public void testIntDigits() throws Exception
	{
		assertEquals( 1000, getParser("1000").intDigits(false).intValue() );
		assertEquals( -1000, getParser("1000").intDigits(true).intValue() );
		assertEquals( "10000000000000000000", getParser("10000000000000000000").intDigits(false).toString() );
		assertEquals( "-10000000000000000000", getParser("10000000000000000000").intDigits(true).toString() );
		
		try {
			getParser("").intDigits(true);
			fail();
		}catch(Exception e) {;}
	}
	
	public void testDecimalDigits() throws Exception
	{
		ISO8601Parser p;
		
		p = getParser("10.25");
		p.decimalDigits(false);
		assertEquals( p.mSecond.intValue(), 10250 );	// milli seconds
		
		p = getParser("90");
		p.decimalDigits(false);
		assertEquals( p.mSecond.intValue(), 90000 );	// milli seconds
		
		p = getParser("0.00001");
		p.decimalDigits(false);
		assertEquals( p.mSecond, new BigDecimal("0.01") );	// milli seconds
	}
	
	public void testNN() {
		System.out.println("testNN");
		// Add your test code here.
	}
	
	public void testTimeZoneModifier() {
		System.out.println("testTimeZoneModifierV");
		// Add your test code here.
	}
	
	public void testTime() {
		System.out.println("testTimeL");
		// Add your test code here.
	}
	
	public void testYear() {
		System.out.println("testYearL");
		// Add your test code here.
	}
	
	public void testMonth() {
		System.out.println("testMonth");
		// Add your test code here.
	}
	
	public void testDate() {
		System.out.println("testDateV");
		// Add your test code here.
	}
	
	public void testDateTimeType() {
		System.out.println("testDateTimeTypeV");
		// Add your test code here.
	}
	
	public void testTimeType() {
		System.out.println("testTimeTypeV");
		// Add your test code here.
	}
	
	public void testDateType() {
		System.out.println("testDateTypeV");
		// Add your test code here.
	}
	
	public void testYearMonthType() {
		System.out.println("testYearMonthTypeV");
		// Add your test code here.
	}
	
	public void testYearType() {
		System.out.println("testYearTypeV");
		// Add your test code here.
	}
	
	public void testMonthDayType() {
		System.out.println("testMonthDayTypeL");
		// Add your test code here.
	}
	
	public void testDayType() {
		System.out.println("testDayTypeL");
		// Add your test code here.
	}
	
	public void testMonthType() {
		System.out.println("testMonthTypeL");
		// Add your test code here.
	}
}
