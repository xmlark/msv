package com.sun.tranquilo.datatype;

/**
 * "negativeInteger" and negativeInteger-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#negativeInteger for the spec
 * 
 * v.isNegative is certainly faster than compareTo(ZERO).
 * This the sole reason why this class exists at all.
 */
public class NegativeIntegerType extends IntegerType
{
	public static final NegativeIntegerType theInstance = new NegativeIntegerType();
	private NegativeIntegerType() { super("negativeInteger"); }
	
	public Object convertValue( String lexicalValue, ValidationContextProvider context )
	{
		Object o = super.convertToValue(lexicalValue,context);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isNegative() )	return null;
		return v;
	}
}
