/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

import com.sun.msv.datatype.datetime.ISO8601Parser;
import com.sun.msv.datatype.datetime.IDateTimeValueType;
import com.sun.msv.datatype.datetime.TimeZone;
import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import org.relaxng.datatype.ValidationContext;

/**
 * base implementation of dateTime and dateTime-truncated types.
 * this class uses IDateTimeValueType as the value object.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DateTimeBaseType extends ConcreteType implements Comparator {
	
	protected DateTimeBaseType(String typeName) {
		super(typeName);
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
	
	/** formats time zone specifier. */
	protected String formatTimeZone( TimeZone tz ) {
		if(tz==null)		return "";	// no time zone
		if(tz.minutes==0)	return "Z";	// GMT
		
		return (tz.minutes<0?"-":"+")+
			formatTwoDigits(new Integer(Math.abs(tz.minutes/60)))+":"+
			formatTwoDigits(new Integer(Math.abs(tz.minutes)%60));
	}
	

}
