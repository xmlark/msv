package com.sun.tranquilo.datatype;

/**
 * "nonPositiveInteger" and nonPositiveInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#nonPositiveInteger for the spec
 */
public class NonPositiveIntegerType extends IntegerType
{
	public static final NonPositiveIntegerType theInstance = new NonPositiveIntegerType();
	private NonPositiveIntegerType() { super("nonPositiveInteger"); }
	
	public Object convertValue( String lexicalValue )
	{
		Object o = super.convertToValue(lexicalValue);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isNonPositive() )	return null;
		return v;
	}
}
