package com.sun.tranquilo.datatype.datetime;

import java.math.BigInteger;
import java.math.BigDecimal;

public class TimeDurationFactory
{
	public static ITimeDurationValueType create(
		Number year, Number month, Number day, Number hour, Number minute, Number mSecond )
	{
		// TODO : support SmallTimeDurationValue
		
		BigDecimal second;
		
		if( mSecond==null )	second=null;
		else
		if( mSecond instanceof BigInteger )
			second = ((BigDecimal)mSecond).movePointLeft(3);
		else
			second = new BigDecimal(mSecond.toString()).movePointLeft(3);
		
		return new BigTimeDurationValueType(
			convertToBigInteger(year),
			convertToBigInteger(month),
			convertToBigInteger(day),
			convertToBigInteger(hour),
			convertToBigInteger(minute),
			second );
	}
	
	private static BigInteger convertToBigInteger( Number n )
	{
		if(n==null)						return null;
		if(n instanceof BigInteger)		return (BigInteger)n;
		else							return new BigInteger(n.toString());
	}
}