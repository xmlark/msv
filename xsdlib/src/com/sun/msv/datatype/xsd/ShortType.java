package com.sun.tranquilo.datatype;

/**
 * "short" and short-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#short for the spec
 */
public class ShortType extends IntegerDerivedType
{
	public static final ShortType theInstance = new ShortType();
	private ShortType() { super("short"); }
	
	public Object convertToValue( String lexicalValue )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Short(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
