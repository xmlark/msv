package com.sun.tranquilo.datatype;

/**
 * "unsignedByte" and unsignedByte-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedByte for the spec
 */
public class UnsignedByteType extends IntegerDerivedType
{
	public static final UnsignedByteType theInstance = new UnsignedByteType();
	private UnsignedByteType() { super("unsignedByte"); }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final short upperBound = 255;

	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			Short v = new Short(lexicalValue);
			if(v==null)						return null;
			if( v.shortValue()<0 )          return null;
			if( v.shortValue()>upperBound ) return null;
			return v;
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
