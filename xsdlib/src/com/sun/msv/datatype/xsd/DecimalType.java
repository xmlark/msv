package com.sun.tranquilo.datatype;

import java.math.BigDecimal;
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
	
	/** constant */
	private static final BigInteger the10 = new BigInteger("10");

	protected boolean checkFormat( String content, ValidationContextProvider context )
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
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		try
		{
			// BigDecimal accepts expressions like "1E4",
			// but XML Schema doesn't.
			
			// so call checkFormat to make sure that
			// format is XML Schema spec compliant.
			if(!checkFormat(content,context))		return null;

			// XML Schema allows optional leading '+' sign,
			// but BigDecimal doesn't.
			// so remove it here.
			
			if( content.length()==0 )		return null;
			
			if( content.charAt(0)=='+' )
				content = content.substring(1);
			
			BigDecimal r = new BigDecimal(content);
			
			// BigDecimal treats 0 != 0.0
			// to workaround this, "normalize" BigDecimal;
			// that is, trailing zeros in fractional digits are removed.
			while(r.scale()>0)
			{
				BigInteger[] q_r = 
					r.unscaledValue().divideAndRemainder(the10);
				
				if( !q_r[1].equals(BigInteger.ZERO) )	break;
				
				r = new BigDecimal(q_r[0], r.scale()-1);
			}
			
			return r;
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
