package com.sun.tranquilo.datatype;

import java.math.BigInteger;

/**
 * one part of YYYY/MM/DD triplet.
 *
 * DateComponent can only hold a value within the range of int.
 * This class does not provide any validation
 *
 *<p>
 * This class provides comparision with other DateComponent,
 * with TimeZoneComponent in mind.
 *
 *<p>
 * Order relation of DateComponent is partial.
 * 
 * a.compareTo(b) a.equals(b)   relation
 * -------------------------------------
 * negative          (false)     a<b
 * positive          (false)     a>b
 *        0          true        a=b
 *        0          false       a<>b
 */
public class DateComponent implements Comparable
{
	public int value;
	public TimeZoneComponent timeZone;
	
	public DateComponent( int value, TimeZoneComponent timeZone )
	{
		this.value		= value;
		this.timeZone	= timeZone;
	}
	
	public DateComponent( int value )
	{
		this( value, null );
	}
	
	public int compareTo( Object o )
	{
		DateComponent rhs = (DateComponent)o;
		return value-rhs.value;
	}
	
	public boolean equals( Object o )
	{
		DateComponent rhs = (DateComponent)o;
		if( value!=rhs.value )	return false;
		
		if( timeZone==null && rhs.timeZone==null )	return true;
		if( timeZone!=null && rhs.timeZone!=null )
			return timeZone.equals(rhs.timeZone);
		
		// if one has time zone but the other doesn't, then it's not the same.
		return false;
	}
	
	public int hashCode()
	{
		return new Integer(value).hashCode()*(timeZone!=null?timeZone.hashCode():1);
	}
}