package com.sun.tranquilo.datatype;

/**
 * "nonNegativeInteger" and nonNegativeInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#nonNegativeInteger for the spec
 */
public class NonNegativeIntegerType extends IntegerType
{
	public static final NonNegativeIntegerType theInstance = new NonNegativeIntegerType();
	private NonNegativeIntegerType() { super("nonNegativeInteger"); }
	
	public Object convertValue( String lexicalValue, ValidationContextProvider context )
	{
		Object o = super.convertToValue(lexicalValue,context);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isNonNegative() )	return null;
		return v;
	}
}
