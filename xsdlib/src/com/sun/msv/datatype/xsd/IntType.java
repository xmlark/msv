package com.sun.tranquilo.datatype;

/**
 * "int" and int-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#int for the spec
 */
public class IntType extends IntegerDerivedType
{
	public static final IntType theInstance = new IntType("int");
	protected IntType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Integer(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
