package com.sun.tranquilo.datatype;

import junit.framework.*;
import com.sun.tranquilo.datatype.conformance.DummyContextProvider;

public class UnionTypeTest extends TestCase
{
	public UnionTypeTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(UnionTypeTest.class);
	}

	private UnionType createUnion( String newName,
		DataType type1, DataType type2, DataType type3 )
			throws BadTypeException
	{
		return (UnionType)DataTypeFactory.deriveByUnion(
			newName, new DataTypeImpl[]{
				(DataTypeImpl)type1,
				(DataTypeImpl)type2,
				(DataTypeImpl)type3});
	}
	
	private UnionType createUnion( String newName,
		String type1, String type2, String type3 )
			throws BadTypeException
	{
		return createUnion( newName,
			DataTypeFactory.getTypeByName(type1),
			DataTypeFactory.getTypeByName(type2),
			DataTypeFactory.getTypeByName(type3) );
	}
	
	/** test get method */
	public void testIsAtomType() throws BadTypeException
	{
		// union is not an atom
		assert(!createUnion( "test", "string", "integer", "QName" ).isAtomType());
	}
	
	/** test verify method */
	public void testVerify() throws BadTypeException
	{
		// this test is naive, and we need further systematic testing.
		// but better something than nothing.
		DataType u = createUnion(null,"integer","QName","yearMonth");
		
		assert( u.verify("1520",DummyContextProvider.theInstance) );
		assert( u.verify("foo:role",DummyContextProvider.theInstance) );
		assert( u.verify("2000-05",DummyContextProvider.theInstance) );
	}
	
	/** test convertToObject method */
	public void testConvertToObject() throws BadTypeException
	{
		DataType tf = DataTypeFactory.getTypeByName("float");
		DataType td = DataTypeFactory.getTypeByName("date");
		DataType th = DataTypeFactory.getTypeByName("hexBinary");
		
		DataType tu = createUnion("myTest", tf, td, th );
		
		assertEquals(
			tu.convertToValueObject("2.000",DummyContextProvider.theInstance),
			tf.convertToValueObject("2.000",DummyContextProvider.theInstance) );
		assertEquals(
			tu.convertToValueObject("2001-02-20",DummyContextProvider.theInstance),
			td.convertToValueObject("2001-02-20",DummyContextProvider.theInstance) );
		assertEquals(
			tu.convertToValueObject("1f5280",DummyContextProvider.theInstance),
			th.convertToValueObject("1F5280",DummyContextProvider.theInstance) );
	}
}
