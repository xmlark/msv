package com.sun.tranquilo.datatype;

import java.math.BigInteger;

/**
 * "decimal" and decimal-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#decimal for the spec
 */
public class DecimalType extends ConcreteType implements Comparator
{
	public static final DecimalType theInstance = new DecimalType();
	private DecimalType() { super("decimal"); }

	protected boolean checkFormat( String content )
	{
		final int len = content.length();
		int i=0;
		char ch;
		boolean atLeastOneDigit = false;
		
		if(len==0)	return false;		// length 0 is not allowed
		
		// leading optional sign
		ch = content.charAt(0);
		if(ch=='-' || ch=='+')	i++;
		
		while(i<len)
		{
			ch = content.charAt(i++);
			if('0'<=ch && ch<='9')
			{
				atLeastOneDigit = true;
				continue;
			}
			if(ch=='.')	break;
			return false;		// other characters are error
		}
		
		while(i<len)
		{// fractional part
			ch = content.charAt(i++);
			if('0'<=ch && ch<='9')
			{
				atLeastOneDigit = true;
				continue;
			}
			return false;	// other characters are error
		}
		
		return atLeastOneDigit;	// at least one digit must be present.
	}
	
	public Object convertToValue( String lexicalValue )
	{
		try
		{
			return new BigInteger(lexicalValue);
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

	public final int compare( Object o1, Object o2 )
	{
		final int r = ((Comparable)o1).compareTo(o2);
		if(r<0)	return LESS;
		if(r>0)	return GREATER;
		return EQUAL;
	}
}
