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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.sun.msv.datatype.xsd.Comparator;

/**
 * tests Util.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UtilTest extends TestCase
{    
	public UtilTest(String testName) { super(testName); }
	
	public static void main(String[] args)
	{
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite()
	{
		return new TestSuite(UtilTest.class);
	}
	
	/** Test of objEqual method, of class com.sun.msv.datatype.datetime.Util. */
	public void testObjEqual()
	{
		assertEquals( true,	Util.objEqual(null,null) );
		assertEquals( false,Util.objEqual(this,null) );
		assertEquals( false,Util.objEqual(null,this) );
		assertEquals( true,	Util.objEqual(this,this) );
		assertEquals( true,	Util.objEqual("12","12") );
	}
	
	/** Test of objHashCode method, of class com.sun.msv.datatype.datetime.Util. */
	public void testObjHashCode()
	{
		assertEquals( this.hashCode(),	Util.objHashCode(this) );
		assertEquals( Util.objHashCode(null), Util.objHashCode(null) );
	}
	
	/** Test of objCompare method, of class com.sun.msv.datatype.datetime.Util. */
	public void testObjCompare()
	{
		assertEquals( Comparator.EQUAL,		Util.objCompare(null,null) );
		assertEquals( Comparator.LESS,		Util.objCompare(new Integer(10), new Integer(20)) );
		assertEquals( Comparator.GREATER,	Util.objCompare(new Integer(25), new Integer(20)) );
		assertEquals( Comparator.UNDECIDABLE,Util.objCompare(null,new Integer(1)) );
		assertEquals( Comparator.UNDECIDABLE,Util.objCompare(new Integer(1),null) );
	}
	
	/** Test of int2bi method, of class com.sun.msv.datatype.datetime.Util. */
	public void testInt2bi()
	{
		assertEquals( Util.int2bi(15), new BigInteger("15") );
		assertEquals( Util.int2bi(new Integer(15)), new BigInteger("15") );
	}
	
	/** Test of maximumDayInMonthFor method, of class com.sun.msv.datatype.datetime.Util. */
	public void testMaximumDayInMonthFor()
	{
		int[] test = new int[]{
			2000, 0/*Jan*/,		31,
			2000, 1/*Feb*/,		29,
			1996, 1/*Feb*/,		29,
			1900, 1/*Feb*/,		28,
			   0, 1/*Feb*/,		29,
			-400, 1/*Feb*/,		29,
			-397, 1/*Feb*/,		28 };
			
		for( int i=0; i<test.length; i+=3 )
		{
			assertEquals( test[i+2], Util.maximumDayInMonthFor(test[i],test[i+1]) );
			assertEquals( test[i+2], Util.maximumDayInMonthFor(Util.int2bi(test[i]),test[i+1]) );
		}
	}
	
}
