/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.datetime.ISO8601Parser;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.BigDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.TimeZone;
import org.relaxng.datatype.ValidationContext;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.SimpleTimeZone;

/**
 * base implementation of dateTime and dateTime-truncated types.
 * this class uses IDateTimeValueType as the value object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class DateTimeBaseType extends ConcreteType implements Comparator {
	
	protected DateTimeBaseType(String typeName) {
		super(typeName);
	}
	
	final public XSDatatype getBaseType() {
		return SimpleURType.theInstance;
	}
	
	private static final ISO8601Parser getParser( String content ) throws Exception {
		return new ISO8601Parser( new ByteArrayInputStream( content.getBytes("UTF8") ) );
	}
	
	protected final boolean checkFormat( String content, ValidationContext context ) {
		// string derived types should use convertToValue method to check its validity
		try {
			runParserL(getParser(content));
			return true;
		} catch( Throwable e ) {
			return false;
		}
	}
	
	/** invokes the appropriate lexical parse method to check lexical format */
	abstract protected void runParserL( ISO8601Parser p ) throws Exception;

	
	public final Object convertToValue( String content, ValidationContext context ) {
		// for string, lexical space is value space by itself
		try {
			return runParserV(getParser(content));
		} catch( Throwable e ) {
			return null;
		}
	}


	/** invokes the appropriate value creation method to obtain value object */
	abstract protected IDateTimeValueType runParserV( ISO8601Parser p ) throws Exception;
	
	/** compare two DateTimeValueType */
	public int compare( Object lhs, Object rhs ) {
		return ((IDateTimeValueType)lhs).compare((IDateTimeValueType)rhs);
	}
	
	public final int isFacetApplicable( String facetName ) {
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_MAXINCLUSIVE)
		||	facetName.equals(FACET_MAXEXCLUSIVE)
		||	facetName.equals(FACET_MININCLUSIVE)
		||	facetName.equals(FACET_MINEXCLUSIVE) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}

	/**
	 * formats an integer into the year representation.
	 * That is, at least four digits and no year 0.
	 */
	protected String formatYear( int year ) {
		String s;
		if( year<=0 )	// negative value
			s = Integer.toString(1-year);
		else			// positive value
			s = Integer.toString(year);
		
		while(s.length()<4)			s = "0"+s;
		if( year<=0 )				s = "-"+s;
		return s;
	}
	
	/**
	 * formats BigInteger into year representation.
	 * 
	 * That is, at least four digits and no year 0.
	 */
	protected String formatYear( BigInteger year ) {
		String s;
		if( year.signum()<=0 )
			// negative value
			s = year.negate().add(BigInteger.ONE).toString();
		else
			// positive value
			s = year.toString();
		
		while(s.length()<4)			s = "0"+s;
		if( year.signum()<=0 )		s = "-"+s;
		return s;
	}
	
	protected String formatTwoDigits( Integer v ) {
		return formatTwoDigits(v,0);
	}
	
	/** formats Integer into two-character-wide string. */
	protected String formatTwoDigits( Integer v, int offset ) {
		if(v==null)		return "00";
		return formatTwoDigits(v.intValue()+offset);
	}
	
	protected String formatTwoDigits( int n ) {
		// n is always non-negative.
		if(n<10)		return "0"+n;
		else			return Integer.toString(n);
	}
	
	/** formats BigDecimal into two- -wide string. */
	protected String formatSeconds( java.math.BigDecimal dec ) {
		if(dec==null)	return "00";
		
		String s = dec.toString();
		if( dec.compareTo( new java.math.BigDecimal("10") ) < 0 )
			s = "0"+s;
		return s;
	}
	
	protected String formatSeconds( Calendar cal ) {
		StringBuffer result = new StringBuffer();
		result.append(formatTwoDigits(cal.get(cal.SECOND)));
		if( cal.isSet(cal.MILLISECOND) ) {// milliseconds
			String ms = Integer.toString(cal.get(cal.MILLISECOND));
			while(ms.length()<3)	ms = "0"+ms;	// left 0 paddings.
			
			result.append('.');
			result.append(ms);
		}
		return result.toString();
	}
	
	/** formats time zone specifier. */
	protected String formatTimeZone( TimeZone tz ) {
		if(tz==null)		return "";	// no time zone
		if(tz.minutes==0)	return "Z";	// GMT
		
		return (tz.minutes<0?"-":"+")+
			formatTwoDigits(new Integer(Math.abs(tz.minutes/60)))+":"+
			formatTwoDigits(new Integer(Math.abs(tz.minutes)%60));
	}

	/** formats time zone specifier. */
	protected String formatTimeZone( Calendar cal ) {
		// TODO: is it possible for the getTimeZone method to return null?
		if( cal.getTimeZone()==null ) return "";
		
		StringBuffer result = new StringBuffer();
		int offset = cal.getTimeZone().getRawOffset();
		if(offset>=0)	result.append('+');
		else {
			result.append('-');
			offset *= -1;
		}
			
		result.append(formatTwoDigits(offset/60));
		result.append(':');
		result.append(formatTwoDigits(offset%60));
		
		return result.toString();
	}

	
	
	
	/** converts Number to integer. null object is considered as 0 */
	protected static int nullAsZero( Number n ) {
		if(n==null)		return 0;
		else			return n.intValue();
	}
	
	/** creates the equivalent Java TimeZone object from BigDateTimeValueType. */
	protected java.util.TimeZone createJavaTimeZone( BigDateTimeValueType v ) {
		if(v.getTimeZone()!=null)
			return new SimpleTimeZone( v.getTimeZone().minutes*60*1000, "custom" );
		else
			// if the time zone is not present, assume the system default.
			return SimpleTimeZone.getDefault();
	}
	
	/** converts our DateTimeValueType to a java-friendly Date type. */
	public final Object createJavaObject( String literal, ValidationContext context ) {
		BigDateTimeValueType v = ((IDateTimeValueType)createValue(literal,context)).getBigValue();
		if(v==null)		return null;
		
		// set fields of Calendar.
		// In BigDateTimeValueType, the first day of the month is 0,
		// where it is 1 in java.util.Calendar.
		
		Calendar cal = new java.util.GregorianCalendar(createJavaTimeZone(v));
		cal.clear();	// reset all fields. This method does not reset the time zone.
		
		if( v.getYear()!=null )		cal.set( cal.YEAR, v.getYear().intValue() );
		if( v.getMonth()!=null )	cal.set( cal.MONTH, v.getMonth().intValue() );
		if( v.getDay()!=null )		cal.set( cal.DAY_OF_MONTH, v.getDay().intValue()+1/*offset*/ );
		if( v.getHour()!=null )		cal.set( cal.HOUR_OF_DAY, v.getHour().intValue() );
		if( v.getMinute()!=null )	cal.set( cal.MINUTE, v.getMinute().intValue() );
		if( v.getSecond()!=null ) {
			cal.set( cal.SECOND, v.getSecond().intValue() );
			cal.set( cal.MILLISECOND, v.getSecond().movePointRight(3).intValue()%1000 );
		}
		
		return cal;
	}

	// since we've overrided the createJavaObject method, the serializeJavaObject method
	// needs to be overrided, too.
	public abstract String serializeJavaObject( Object value, SerializationContext context );
	
	public Class getJavaObjectType() {
		return Calendar.class;
	}
}
