package com.sun.tranquilo.datatype;

import junit.framework.*;

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