package com.sun.tranquilo.datatype.datetime;

import java.math.BigInteger;

public class Util
{
	// frequently used constants
	protected static final BigInteger the4  = new BigInteger("4");
	protected static final BigInteger the10 = new BigInteger("10");
	protected static final BigInteger	the12 = new BigInteger("12");
	protected static final BigInteger	the24 = new BigInteger("24");
	protected static final BigInteger the60 = new BigInteger("60");
	protected static final BigInteger the100= new BigInteger("100");
	protected static final BigInteger the400= new BigInteger("400");

	protected static TimeZone timeZonePos14 = TimeZone.create(14*60);
	protected static TimeZone timeZoneNeg14 = TimeZone.create(-14*60);
		

	/** compare two objects
	 * 
	 * @return	true
	 *	<ul>
	 *		<li> if both are null
	 *		<li> if both are non-null and o1.equals(o2)
	 *  </ul>
	 * false otherwise.
	 */
	protected static boolean objEqual( Object o1, Object o2 )
	{
		if( o1==null && o2==null )	return true;
		if( o1!=null && o2!=null && o1.equals(o2))	return true;
		return false;
	}
	
	protected static int objHashCode( Object o )
	{
		if(o==null)		return 0;
		else			return o.hashCode();
	}
	
	protected static int objCompare( Comparable o1, Comparable o2 )
	{
		if( o1==null && o2==null )	return 0;	// equal
		if( o1!=null && o2!=null )	return o1.compareTo(o2);
		return 0;	// inequal
	}

	/** creates BigInteger that corresponds with v */
	protected static BigInteger int2bi( int v )
	{
		return new BigInteger( Integer.toString(v) );
	}

	protected static BigInteger int2bi( Integer v )
	{
		if( v==null )		return BigInteger.ZERO;
		return new BigInteger( v.toString() );
	}

	private static final int[] dayInMonth = new int[]{31,-1,31,30,31,30,31,  31,30,31,30,31};
	
	public static int maximumDayInMonthFor( int year, int month )
	{
		if( month==1 )
		{
			if( year%400 == 0 )		return 29;
			if( year%4 == 0 && year%100 != 0 )	return 29;
			return 28;
		}
		
		return dayInMonth[month];
	}
	
	public static int maximumDayInMonthFor( BigInteger year, int month )
	{
		if( month==1 )	// Februrary needs special care
		{
			if( year.mod(Util.the400).intValue()==0 )	return 29;
			if( year.mod(Util.the4).intValue()==0 && year.mod(Util.the100).intValue()!=0 )	return 29;
			return 28;
		}
		
		return dayInMonth[month];
	}

}
