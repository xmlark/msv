package com.sun.tranquilo.datatype.datetime;

import java.math.BigInteger;
import java.math.BigDecimal;

/**
 * DateTimeValueType object that can hold all lexically valid dateTime value.
 * 
 * This class provides:
 * <ol>
 *  <li> Unlimited digits for year (e.g., "year 9999999999999999999999")
 *  <li> Unlimited digits for fraction of second (e.g. 0.00000000000001 sec)
 * </ol>
 * 
 * To provide methods that can change date/time values, normalize method
 * should be modified too.
 */
public class BigDateTimeValueType implements IDateTimeValueType
{
	/** year value.
	 * this variable is null if no year is specified.
	 *
	 * Since there is no year 0, value 0 indicates year -1. -1 indicates -2, and so forth.
	 */
	private BigInteger year;
	
	/** month (always between 0 and 11)
	 * this variable is null if no year is specified
	 */
	private Integer month;
	
	/** day (always normalized)
	 * this variable is null if no year is specified
	 */
	private Integer day;
	
	/** hour (always between 0 and 23)
	 * this variable is null if no year is specified
	 */
	private Integer hour;
	
	/** minute (always between 0 and 59)
	 * this variable is null if no year is specified
	 */
	private Integer minute;
	
	/** second (always in [0,60) )
	 * this variable is null if no year is specified
	 */
	private BigDecimal second;
	
	/** time zone specifier */
	private TimeZone zone;
	
	/** creates an instance with the specified BigDateTimeValueType,
	 *  with modified time zone.
	 * 
	 *  created object shares its date/time value component with the original one,
	 *  so special care is necessary not to mutate those values.
	 */
	public BigDateTimeValueType( BigDateTimeValueType base, TimeZone newTimeZone )
	{
		this( base.year, base.month, base.day, base.hour, base.minute, base.second, newTimeZone );
	}
	
	public BigDateTimeValueType( BigInteger year, int month, int day, int hour, int minute, BigDecimal second, TimeZone timeZone )
	{
		this( year, new Integer(month), new Integer(day), new Integer(hour), new Integer(minute), second, timeZone );
	}
	public BigDateTimeValueType( BigInteger year, Integer month, Integer day, Integer hour, Integer minute, BigDecimal second, TimeZone timeZone )
	{
		this.year	= year;
		this.month	= month;
		this.day	= day;
		this.hour	= hour;
		this.minute	= minute;
		this.second	= second;
		this.zone	= timeZone;
	}
	
	public BigDateTimeValueType() {}
	
	
	
	public BigDateTimeValueType getBigValue() { return this; }

	public boolean equals( Object o )
	{ return equals( (IDateTimeValueType)o ); }
	
	public boolean equals( IDateTimeValueType rhs )
	{
		if(!(rhs instanceof BigDateTimeValueType))
			rhs = rhs.getBigValue();
		return equals( this, (BigDateTimeValueType)rhs );
	}
	
	public boolean equals( BigDateTimeValueType lhs, BigDateTimeValueType rhs )
	{
		return compare(lhs,rhs)==EQUAL;
	}
	
	
	public int hashCode()
	{
		// to be consistent with equals method, we have to normalize
		// value before computation.
		BigDateTimeValueType n = this.normalize();
		return Util.objHashCode(n.year) + Util.objHashCode(n.month) + Util.objHashCode(n.day)
			+  Util.objHashCode(n.hour) + Util.objHashCode(n.minute) + Util.objHashCode(n.second)
			+  Util.objHashCode(n.zone);
	}
	
	public int compare( IDateTimeValueType o )
	{
		if(!(o instanceof BigDateTimeValueType) )
			o = o.getBigValue();
		
		return compare( (BigDateTimeValueType)o, this );
	}
	
	protected static int compare( BigDateTimeValueType lhs, BigDateTimeValueType rhs )
	{
		lhs = (BigDateTimeValueType)lhs.normalize();
		rhs = (BigDateTimeValueType)rhs.normalize();
		
		if( (lhs.zone!=null && rhs.zone!=null) || (lhs.zone==null && rhs.zone==null) )
		{
			if(!Util.objEqual(lhs.year,rhs.year))		return Util.objCompare(lhs.year,rhs.year);
			if(!Util.objEqual(lhs.month,rhs.month))		return Util.objCompare(lhs.month,rhs.month);
			if(!Util.objEqual(lhs.day,rhs.day))			return Util.objCompare(lhs.day,rhs.day);
			if(!Util.objEqual(lhs.hour,rhs.hour))		return Util.objCompare(lhs.hour,rhs.hour);
			if(!Util.objEqual(lhs.minute,rhs.minute))	return Util.objCompare(lhs.minute,rhs.minute);
			if(!Util.objEqual(lhs.second,rhs.second))	return Util.objCompare(lhs.second,rhs.second);
		
			return EQUAL;
		}
		
		if( lhs.zone==null )
		{
			if( compareTo( (BigDateTimeValueType)new BigDateTimeValueType(lhs,Util.timeZonePos14).normalize(), rhs ) <= 0 )
				return LESS;	// lhs < rhs
			if( compareTo( (BigDateTimeValueType)new BigDateTimeValueType(lhs,Util.timeZoneNeg14).normalize(), rhs ) >= 0 )
				return GREATER;	// lhs > rhs
			return UNDECIDABLE;		// lhs <> rhs
		}
		else
		{
			if( compareTo( lhs, (BigDateTimeValueType)new BigDateTimeValueType(rhs,Util.timeZoneNeg14) ) <= 0 )
				return LESS;	// lhs < rhs
			if( compareTo( lhs, (BigDateTimeValueType)new BigDateTimeValueType(rhs,Util.timeZonePos14) ) >= 0 )
				return GREATER;	// lhs > rhs
			return UNDECIDABLE;	// lhs <> rhs
		}
	}
	
	/** normalized DateTimeValue of this object.
	 * 
	 * once when the normalized value is computed,
	 * the value is kept in this varible so that
	 * successive calls to normalize method need not
	 * have to compute it again.
	 * 
	 * This approach assumes that modification to the date/time component
	 * will never be made.
	 */
	private IDateTimeValueType normalizedValue = null;
	
	public IDateTimeValueType normalize()
	{
		// see if this object is already normalized
		if( zone==null || zone.minutes==0 )		return this;
		
		// see if there is cached normalized value
		if( normalizedValue!=null )			return normalizedValue;
		
		// faster performance can be achieved by writing optimized inline addition code.
		normalizedValue = 
			this.add( BigTimeDurationValueType.fromMinutes(-zone.minutes) );
		
		return normalizedValue;
	}
	
	
	
	public IDateTimeValueType add( ITimeDurationValueType _rhs )
	{
		if( _rhs instanceof BigTimeDurationValueType )
		{// big + big
			BigTimeDurationValueType rhs = (BigTimeDurationValueType)_rhs;

			BigInteger[] quoAndMod = Util.int2bi(this.month).add(rhs.month).divideAndRemainder(Util.the12);
			
			BigInteger oyear; int omonth;
			int ohour, ominute; BigDecimal osecond;
			
			omonth = quoAndMod[1].intValue();
			oyear = quoAndMod[0].add(this.year).add(rhs.year);
			
			
			BigDecimal sec = this.second.add(rhs.second);
			
			// quo = floor((this.second+rhs.second)/60)
			//     = floor( (this.second+rhs.second)*10^scale / (60*10^scale) )
			//     = (this.second+rhs.second).unscaled / 60*10^scale
			
			quoAndMod = sec.unscaledValue().divideAndRemainder(
					Util.the60.multiply(Util.the10.pow(sec.scale())) );
			
			osecond = new BigDecimal(quoAndMod[1], sec.scale());
			
			
			quoAndMod = quoAndMod[0].add(Util.int2bi(this.minute)).add(rhs.minute).divideAndRemainder(Util.the60);
			ominute = quoAndMod[1].intValue();
			
			quoAndMod = quoAndMod[0].add(Util.int2bi(this.hour)).add(rhs.hour).divideAndRemainder(Util.the24);
			ohour = quoAndMod[1].intValue();
			
			int tempDays;
			int md = maximumDayInMonthFor(oyear,omonth);
			{
				int dayValue = (this.day!=null)?this.day.intValue():0;
				if( dayValue<0 )		tempDays=0;
				else if( dayValue>=md )	tempDays=md-1;
				else					tempDays=dayValue;
			}
			
			BigInteger oday = rhs.day.add(quoAndMod[0]).add(Util.int2bi(tempDays));
			while(true)
			{
				int carry;
				if( oday.signum()==-1 )	// day<0
				{
					oday = oday.add(Util.int2bi(maximumDayInMonthFor(oyear,omonth-1)));
					carry = -1;
				}
				else
				{
					BigInteger bmd = Util.int2bi(maximumDayInMonthFor(oyear,omonth));
					if( oday.compareTo(bmd)>=0 )
					{
						oday = oday.subtract(bmd);
						carry = +1;
					}
					else
						break;
				}
				
				omonth += carry;
				oyear = oyear.add( Util.int2bi(omonth/12) );
				omonth %= 12;
			}
			
			return new BigDateTimeValueType( oyear, omonth, oday.intValue(), ohour, ominute, osecond, this.zone );
		}
		else
		{// big + small
			// TODO : implement this to achive better performance
			
			// just for now, convert it to BigTimeDurationValue and then compute the result.
			return add( _rhs.getBigValue() );
		}
	}
}
