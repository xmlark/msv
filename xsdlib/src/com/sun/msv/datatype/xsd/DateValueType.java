package com.sun.tranquilo.datatype;


public class DateValueType implements Comparable
{
	public final DateComponent		year;
	public final DateComponent		month;
	public final DateComponent		day;
	public final TimeZoneComponent	timeZone;
	
	public DateValueType( DateComponent year, DateComponent month,
		DateComponent day, TimeZoneComponent timeZone )
	{
		this.year		= year;
		this.month		= month;
		this.day		= day;
		this.timeZone	= timeZone;
		
		year.timeZone = month.timeZone = day.timeZone = timeZone;
	}
	
	public DateValueType normalize()
	{
	}
	
	public boolean equals( Object o )
	{
		DateValueType rhs = (DateValueType)o;
		
		if(year.equals(rhs.year)
		&& month.equals(rhs.month)
		&& day.equals(rhs.day) )
		{
			if( timeZone==null && rhs.timeZone==null )	return true;
			if( timeZone==null || rhs.timeZone==null )	return false;
			return timeZone.equals(rhs.timeZone);
		}
		
		return false;
	}
	
	public int compareTo( Object o )
	{
		DateValueType rhs = (DateValueType)o;
		
		int r;
		
		r = year.compareTo(rhs.year);
		if(r!=0)	return r;
		
		r = month.compareTo(rhs.month);
		if(r!=0)	return r;
		
		r = day.compareTo(rhs.day);
		if(r!=0)	return r;
		
		return 0;		// equal or undecidable.
	}
	
	public int hashCode()
	{
		return year.hashCode()*month.hashCode()*day.hashCode()*
			(timeZone!=null?timeZone.hashCode():1);
	}
}