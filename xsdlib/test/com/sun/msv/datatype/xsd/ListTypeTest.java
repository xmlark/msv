/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import junit.framework.*;
import com.sun.msv.datatype.xsd.conformance.DummyContextProvider;
import org.relaxng.datatype.DatatypeException;

/**
 * tests ListType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ListTypeTest extends TestCase
{
	public ListTypeTest( String name ) { super(name); }
	
	public static void main(java.lang.String[] args) {
		junit.textui.TestRunner.run(suite());
	}
	
	public static Test suite() {
		return new TestSuite(ListTypeTest.class);
	}

	private ListType createList( String newName, XSDatatype itemType ) throws DatatypeException
	{
		return (ListType)DatatypeFactory.deriveByList(newName,itemType);
	}
	
	private ListType createList( String newName, String itemType ) throws DatatypeException
	{
		return createList( newName, DatatypeFactory.getTypeByName(itemType) );
	}
	
	/** test getVariety method */
	public void testGetVariety() throws DatatypeException
	{
		// list is not an atom
		assertEquals( XSDatatype.VARIETY_LIST,
			createList( "test", "string" ).getVariety() );
	}
	
	/** test verify method */
	public void testVerify() throws DatatypeException
	{
		// this test is naive, and we need further systematic testing.
		// but better something than nothing.
		XSDatatype t = createList("test","short");
		
		assert( t.isValid("  12  \t13 \r\n14\n \t   5  99  ",
			DummyContextProvider.theInstance ));
		assert(!t.isValid("  51 2 6 fff  ",
			DummyContextProvider.theInstance ));
		
		assert( t.isValid("",	// this should be considered as a length 0 list
			DummyContextProvider.theInstance ));
		assert( t.isValid(" \t \n ",
			DummyContextProvider.theInstance ));
	}
	
	/** test convertToObject method */
	public void testConvertToObject() throws DatatypeException
	{
		XSDatatype t = createList("myTest", "string" );

		ListValueType v = (ListValueType)
			t.createValue("  a b  c",DummyContextProvider.theInstance);
		
		assert(v.values.length==3);
		assertEquals(v.values[0],"a");
		assertEquals(v.values[1],"b");
		assertEquals(v.values[2],"c");
	}
}
