/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import junit.framework.*;

/**
 * tests FractionDigitsFacet.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FractionDigitsFacetTest extends TestCase
{
	public FractionDigitsFacetTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(FractionDigitsFacetTest.class);
	}
	
	public void testCountScale()
	{
		assertEquals( 0, FractionDigitsFacet.countScale("5.000000000000") );
		assertEquals( 0, FractionDigitsFacet.countScale("-95") );
		assertEquals( 1, FractionDigitsFacet.countScale("5.9") );
		assertEquals( 1, FractionDigitsFacet.countScale("99925.900") );
		assertEquals( 5, FractionDigitsFacet.countScale("6.0000400") );
		assertEquals( 5, FractionDigitsFacet.countScale("6.0030400") );
	}
}
