/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd.datetime;

import java.math.BigDecimal;
import java.math.BigInteger;

import com.sun.msv.datatype.xsd.Comparator;

/**
 * ITimeDurationValueType implementation that can hold all lexically legal
 * timeDuration value.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class BigTimeDurationValueType implements ITimeDurationValueType {
	
	protected BigInteger year;
	protected BigInteger month;
	protected BigInteger day;
	protected BigInteger hour;
	protected BigInteger minute;
	protected BigDecimal second;

	private static final BigDateTimeValueType[] testInstance =
		new BigDateTimeValueType[]{
			new BigDateTimeValueType(
				new BigInteger("1696"), 8/*Sep*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.GMT ),
			new BigDateTimeValueType(
				new BigInteger("1697"), 1/*Feb*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.GMT ),
			new BigDateTimeValueType(
				new BigInteger("1903"), 2/*Mar*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.GMT ),
			new BigDateTimeValueType(
				new BigInteger("1903"), 6/*Jul*/, 0/*1st*/, 0,0, new BigDecimal(0), TimeZone.GMT ) };
																				

	public boolean equals( Object o ) {
		return equals( (ITimeDurationValueType)o );
	}
	public boolean equals( ITimeDurationValueType o ) {
		return compare(o)==Comparator.EQUAL;
	}
	public String toString() {
		return	((year==null||year.signum()<0)?"-":"")+
				"P"+nullAsZero(year).abs()+"Y"+
				nullAsZero(month)+"M"+
				nullAsZero(day)+"DT"+
				nullAsZero(hour)+"H"+
				nullAsZero(minute)+"M"+
				(second==null?"":second.toString())+"S";
	}
	
	private BigInteger nullAsZero(BigInteger o) {
		if(o==null)	return BigInteger.ZERO;
		else		return o;
	}
	
	/**
	 * hash code has to be consistent with equals method.
	 */
	public int hashCode() {
		// 400Y = 365D*303 + 366D*97 = 146097D = 3506328 hours
		// = 210379680 minutes
		// and no other smaller years have their equivalent days.
		
		// hashCode is very complex because it has to consistent with the behavior of equals method.
		return nullAsZero(day).multiply(Util.the24)
			.add( nullAsZero(hour) ).multiply(Util.the60)
				.add( nullAsZero(minute) ).mod(Util.the210379680).hashCode();
	}

	public int compare( ITimeDurationValueType o ) {
		if(!(o instanceof BigTimeDurationValueType) )
			o = o.getBigValue();

		return compare( this, (BigTimeDurationValueType)o );
	}
	
	static private int compare( BigTimeDurationValueType lhs, BigTimeDurationValueType rhs ) {
		boolean less=false,greater=false,noDeterminate=false;
		
		for( int i=0; i<testInstance.length; i++ ) {
			BigDateTimeValueType l = (BigDateTimeValueType)testInstance[i].add(lhs);
			BigDateTimeValueType r = (BigDateTimeValueType)testInstance[i].add(rhs);
			
			int v = BigDateTimeValueType.compare(l,r);
			
			if(v<0)						less=true;
			if(v>0)						greater=true;
			if(v==0) {
				if(!l.equals(r))		noDeterminate=true;
			}
		}
		
		if(noDeterminate)		return Comparator.UNDECIDABLE;
		if(less && greater)		return Comparator.UNDECIDABLE;
		if(less)				return Comparator.LESS;		// lhs<rhs
		if(greater)				return Comparator.GREATER;	// lhs>rhs
		return Comparator.EQUAL;
	}

	public BigTimeDurationValueType getBigValue() { return this; }

	public BigTimeDurationValueType(
		BigInteger year, BigInteger month, BigInteger day,
		BigInteger hour, BigInteger minute, BigDecimal second ) {
		this.year	= year!=null?year:BigInteger.ZERO;
		this.month	= month!=null?month:BigInteger.ZERO;
		this.day	= day!=null?day:BigInteger.ZERO;
		this.hour	= hour!=null?hour:BigInteger.ZERO;
		this.minute	= minute!=null?minute:BigInteger.ZERO;
		this.second	= second!=null?second:new BigDecimal(0);
	}
	
	public static BigTimeDurationValueType fromMinutes( int minutes )
	{ return fromMinutes(Util.int2bi(minutes)); }
	public static BigTimeDurationValueType fromMinutes( BigInteger minutes )
	{ return new BigTimeDurationValueType(null,null,null,null,minutes,null); }
	

    // serialization support
    private static final long serialVersionUID = 1;    
}
