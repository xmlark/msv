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
		
		if(!(year   instanceof BigInteger))	year  = new BigInteger(year.  toString());
		if(!(month  instanceof BigInteger))	month = new BigInteger(month. toString());
		if(!(day    instanceof BigInteger))	day   = new BigInteger(day.   toString());
		if(!(hour   instanceof BigInteger)) hour  = new BigInteger(hour.  toString());
		if(!(minute instanceof BigInteger)) minute= new BigInteger(minute.toString());
		if( mSecond instanceof BigInteger )
			second = ((BigDecimal)mSecond).movePointLeft(3);
		else
			second = new BigDecimal(mSecond.toString()).movePointLeft(3);
		
		return new BigTimeDurationValueType(
			(BigInteger)year, (BigInteger)month, (BigInteger)day,
			(BigInteger)hour, (BigInteger)minute, second );
	}
}