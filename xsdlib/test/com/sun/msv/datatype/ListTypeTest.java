package com.sun.tranquilo.datatype;

import junit.framework.*;
import com.sun.tranquilo.datatype.conformance.DummyContextProvider;

public class ListTypeTest extends TestCase
{
	public ListTypeTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(ListTypeTest.class);
	}

	private ListType createList( String newName, DataType itemType )
		throws BadTypeException
	{
		return (ListType)DataTypeFactory.deriveByList(newName,itemType);
	}
	
	private ListType createList( String newName, String itemType )
		throws BadTypeException
	{
		return createList( newName, DataTypeFactory.getTypeByName(itemType) );
	}
	
	/** test get method */
	public void testIsAtomType() throws BadTypeException
	{
		// list is not an atom
		assert(!createList( "test", "string" ).isAtomType());
	}
	
	/** test verify method */
	public void testVerify() throws BadTypeException
	{
		// this test is naive, and we need further systematic testing.
		// but better something than nothing.
		DataType t = createList("test","short");
		
		assert( t.verify("  12  \t13 \r\n14\n \t   5  99  ",
			DummyContextProvider.theInstance ));
		assert(!t.verify("  51 2 6 fff  ",
			DummyContextProvider.theInstance ));
		
		assert( t.verify("",	// this should be considered as a length 0 list
			DummyContextProvider.theInstance ));
		assert( t.verify(" \t \n ",
			DummyContextProvider.theInstance ));
	}
	
	/** test convertToObject method */
	public void testConvertToObject() throws BadTypeException
	{
		DataType t = createList("myTest", "string" );

		ListValueType v = (ListValueType)
			t.convertToValueObject("  a b  c",DummyContextProvider.theInstance);
		
		assert(v.values.length==3);
		assertEquals(v.values[0],"a");
		assertEquals(v.values[1],"b");
		assertEquals(v.values[2],"c");
	}
}
