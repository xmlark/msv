package com.sun.tranquilo.datatype.datetime;

public class DateTimeFactory
{
	public static IDateTimeValueType createFromDate(
		Object year, Object month, Object day, Object zone )
	{
//		if( year instanceof Integer )
//			return new SmallDateTimeValueType( ... );
		
		return new BigDateTimeValueType( year, month, day, null, null, null, zone );
	}
}
