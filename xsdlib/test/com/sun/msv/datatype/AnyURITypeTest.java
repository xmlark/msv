package com.sun.tranquilo.datatype;

import junit.framework.*;

public class AnyURITypeTest extends TestCase
{
	public AnyURITypeTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(AnyURITypeTest.class);
	}
	
	public void testEscape()
	{
		assertEquals( AnyURIType.escape(""), "" );
		assertEquals( AnyURIType.escape("ABCXYZ"), "ABCXYZ" );
		
		assertEquals( AnyURIType.escape(
			new String( new char[]{0x125} ) ),
			"%C4%A5" );	// latin small letter h with circumflex
		
		assertEquals( AnyURIType.escape(
			new String( new char[]{0x937} ) ),
			"%E0%A4%B7" ); // devanagari letter SSA
		
		assertEquals( AnyURIType.escape(
			new String( new char[]{0xD8A5,0xDDC3} ) ),
			"%F0%A9%97%83" );	// #x295C3
		
//		assertEquals( "%23", AnyURIType.escape("#") );
	}
}