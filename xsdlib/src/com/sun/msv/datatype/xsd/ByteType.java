package com.sun.tranquilo.datatype;

import java.math.BigInteger;

/**
 * "byte" and byte-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#byte for the spec
 */
public class ByteType extends IntegerDerivedType
{
	public final static ByteType theInstance = new ByteType();
	private ByteType() { super("byte"); }
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Byte(content);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
