package com.sun.tranquilo.datatype.conformance;

import com.sun.tranquilo.datatype.ValidationContextProvider;

final public class DummyContextProvider implements ValidationContextProvider
{
	private DummyContextProvider() {}
	
	public static final DummyContextProvider theInstance
		= new DummyContextProvider();
	
	public String resolveNamespacePrefix( String prefix )
	{
		if( prefix.equals("foo") )
			return "http://foo.examples.com";
		if( prefix.equals("bar") || prefix.equals("baz") )
			return "http://bar.examples.com";
		if( prefix.equals("") || prefix.equals("emp") )
			return "http://empty.examples.com";
		
		return null;	// undefined
	}
	
	public boolean isUnparsedEntity( String name )
	{
		return name.equals("foo") || name.equals("bar");
	}
}