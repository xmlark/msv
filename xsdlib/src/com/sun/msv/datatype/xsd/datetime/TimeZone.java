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

/**
 * simple time zone component.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class TimeZone implements java.io.Serializable {
	/** difference from GMT in terms of minutes */
	public int minutes;
	
	public static TimeZone GMT			= new TimeZone(0);
	
	private TimeZone( int minutes ) {
		// value must be within -14:00 and 14:00
		if( minutes<-14*60 || minutes>14*60 )
			throw new IllegalArgumentException();
		
		this.minutes = minutes;
	}
	
	public static TimeZone create( int minutes ) {
		if(minutes==0)		return GMT;
		else				return new TimeZone(minutes);
	}
	
	protected Object readResolve() throws java.io.ObjectStreamException {
		// use GMT object in case of GMT.
		if(minutes==0)		return GMT;
		return				this;
	}
	
	public int hashCode()	{ return minutes; }
	public boolean equals( Object o )  {
		return ((TimeZone)o).minutes==this.minutes;
	}

    // serialization support
    private static final long serialVersionUID = 1;    
}
