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
	
	/** test escaping of ASCII characters */
	public void testAsciiEscape()
	{
		assertEquals( AnyURIType.escape(""), "" );
		assertEquals( AnyURIType.escape("ABCXYZ"), "ABCXYZ" );
		
		// those characters may not be escaped.
		assertEquals( AnyURIType.escape("-_.!~*'()[]#%"), "-_.!~*'()[]#%" );
		
		// those characters have to be escaped.
		assertEquals( AnyURIType.escape(" &="), "%20%26%3D" );
	}
	
	/** test %HH escaping of non-ASCII characters. */
	public void testNonAsciiEscape()
	{
		assertEquals( AnyURIType.escape(
			new String( new char[]{0x125} ) ),
			"%C4%A5" );	// latin small letter h with circumflex
		// also known as Planck constant per the speed of light in Physics.
		
		assertEquals( AnyURIType.escape(
			new String( new char[]{0x937} ) ),
			"%E0%A4%B7" ); // devanagari letter SSA
		
		assertEquals( AnyURIType.escape(
			new String( new char[]{0xD8A5,0xDDC3} ) ),
			"%F0%A9%97%83" );	// #x295C3
	}
}