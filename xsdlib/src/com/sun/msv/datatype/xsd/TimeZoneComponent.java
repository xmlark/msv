package com.sun.tranquilo.datatype;


public class TimeZoneComponent implements Comparable
{
	public int	minutes;		// difference from GMT in terms of minutes
	
	public TimeZoneComponent( int minutes )
	{
		this.minutes = minutes;
	}
	
	public boolean equals( Object o )
	{
		return minutes == ((TimeZoneComponent)o).minutes;
	}
	
	public int compareTo( Object o )
	{
		return minutes - ((TimeZoneComponent)o).minutes;
	}
	
	public int hashCode() { return minutes; }
}