package com.sun.msv.grammar.util;

import junit.framework.*;
import com.sun.msv.grammar.*;

public class NameClassSimplifierTest extends TestCase {
	
	public NameClassSimplifierTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(NameClassSimplifierTest.class);
	}
	
	public void testSimplifier1() throws Exception {
		assertSame(
			AnyNameClass.theInstance,
			NameClassSimplifier.simplify(
				new ChoiceNameClass(
					AnyNameClass.theInstance,
					new DifferenceNameClass(
						new NamespaceNameClass("abc"),
						new SimpleNameClass("abc","def")))));
	}
	
	public void testSimplifier2() throws Exception {
		NameClass nc = NameClassSimplifier.simplify(
			new DifferenceNameClass(
				new ChoiceNameClass(
					AnyNameClass.theInstance,
					new NamespaceNameClass("abc")
				),
				new SimpleNameClass("abc","def")));
		
		assert( nc instanceof NotNameClass );
		NotNameClass nnc = (NotNameClass)nc;
		assert( nnc.child instanceof SimpleNameClass );
		SimpleNameClass snc = (SimpleNameClass)nnc.child;
		assertEquals( snc.namespaceURI, "abc" );
		assertEquals( snc.localName, "def");
	}
	
	public void testSimplifier3() throws Exception {
		NameClass nc = NameClassSimplifier.simplify(
			new DifferenceNameClass(
				AnyNameClass.theInstance,
				new DifferenceNameClass(
					new NamespaceNameClass("abc"),
					new SimpleNameClass("abc","def"))));
		
		assert( nc instanceof NotNameClass );
		NotNameClass nnc = (NotNameClass)nc;
		assert( nnc.child instanceof DifferenceNameClass );
		DifferenceNameClass dnc = (DifferenceNameClass)nnc.child;
		assert( dnc.nc1 instanceof NamespaceNameClass );
		NamespaceNameClass nc1 = (NamespaceNameClass)dnc.nc1;
		assertEquals( "abc",nc1.namespaceURI );
		assert( dnc.nc2 instanceof SimpleNameClass );
		SimpleNameClass nc2 = (SimpleNameClass)dnc.nc2;
		assertEquals( "abc",nc2.namespaceURI );
		assertEquals( "def",nc2.localName );
	}
}
