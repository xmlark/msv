package com.sun.tranquilo.datatype;

/**
 * "integer" and integer-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#integer for the spec
 */
public class IntegerType extends IntegerDerivedType
{
	public static final IntegerType theInstance = new IntegerType("integer");
	protected IntegerType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue )
	{
		return IntegerValueType.create(lexicalValue);
	}
}
