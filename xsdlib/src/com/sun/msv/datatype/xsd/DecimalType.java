package com.sun.tranquilo.datatype;

/**
 * "decimal" and decimal-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#decimal for the spec
 */
public class DecimalType extends DataTypeImpl
{
	public static final DecimalType theInstance = new DecimalType();
	private DecimalType() { super("decimal"); }
	
	protected boolean checkFormat( String content )
	{
		try
		{
			new DecimalValueType(content);
			return true;
		}
		catch( NumberFormatException e ) { return false; }
	}
	
	public Object convertToValue( String lexicalValue )
	{
		try
		{
			return new DecimalValueType(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}

	public final int isFacetApplicable( String facetName )
	{
		// TODO : should we allow scale facet, or not?
		if( facetName.equals(FACET_PRECISION)
		||	facetName.equals(FACET_SCALE)
		||	facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_MAXINCLUSIVE)
		||	facetName.equals(FACET_MININCLUSIVE)
		||	facetName.equals(FACET_MAXEXCLUSIVE)
		||	facetName.equals(FACET_MINEXCLUSIVE) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
}
