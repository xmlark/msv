package com.sun.tranquilo.datatype;

/**
 * "unsignedLong" and unsignedLong-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedLong for the spec
 */
public class UnsignedLongType extends IntegerType
{
	public static final UnsignedLongType theInstance = new UnsignedLongType();
	private UnsignedLongType() { super("unsignedLong"); }

    /** upper bound value. this is the maximum possible valid value as an unsigned long */
    private static final IntegerValueType upperBound
		= IntegerValueType.create("18446744073709551615");
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		final IntegerValueType v = IntegerValueType.create(lexicalValue);
		if(v==null)							return null;
		if( !v.isNonNegative() )            return null;
		if( upperBound.compareTo(v)<0 )     return null;
		return v;
	}
}
