package com.sun.tranquilo.datatype;

/**
 * "double" and double-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#double for the spec
 */
public class DoubleType extends FloatingNumberType
{
	public static final DoubleType theInstance = new DoubleType();
	private DoubleType() { super("double"); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// TODO : probably the same problems exist as in the case of float
		try
		{
			if(lexicalValue.equals("NaN"))	return new Double(Double.NaN);
			if(lexicalValue.equals("INF"))	return new Double(Double.POSITIVE_INFINITY);
			if(lexicalValue.equals("-INF"))	return new Double(Double.NEGATIVE_INFINITY);
			
			if(lexicalValue.length()==0
			|| !isDigitOrPeriodOrSign(lexicalValue.charAt(0))
			|| !isDigitOrPeriodOrSign(lexicalValue.charAt(lexicalValue.length()-1)) )
				return null;
			
			
			// these screening process is necessary due to the wobble of Float.valueOf method
			return Double.valueOf(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
