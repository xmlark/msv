package com.sun.tranquilo.datatype;

/**
 * "positiveInteger" and positiveInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#positiveInteger for the spec
 */
public class PositiveIntegerType extends IntegerType
{
	public static final PositiveIntegerType theInstance = new PositiveIntegerType();
	private PositiveIntegerType() { super("positiveInteger"); }
	
	public Object convertValue( String lexicalValue )
	{
		Object o = super.convertToValue(lexicalValue);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isPositive() )	return null;
		return v;
	}
}
