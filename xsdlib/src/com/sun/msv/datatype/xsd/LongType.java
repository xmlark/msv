package com.sun.tranquilo.datatype;

/**
 * "long" and long-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#long for the spec
 */
public class LongType extends IntegerDerivedType
{
	public static final LongType theInstance = new LongType();
	private LongType() { super("long"); }
	protected LongType( String typeName ) { super(typeName); }
	
	public Object convertToValue( String lexicalValue )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Long(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
