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

import java.io.Serializable;
import java.util.SimpleTimeZone;

/**
 * simple time zone component.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class TimeZone implements java.io.Serializable {
    /** difference from GMT in terms of minutes */
    public int minutes;

    /**
     * A singleton instance used to represent "Z" time zone.
     * The value is equivalent to GMT, but using a separate
     * instance allows us to do a correct round-trip.
     */
    public static final TimeZone ZERO = new ZeroTimeZone();
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the ZERO singleton instance. Once again, using a special
     * instance is a hack to make the round-tripping work OK.
     */
    public static final java.util.TimeZone JAVA_TIME_ZONE_ZERO = new JavaZeroTimeZone();
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the missing time zone.
     */
    public static final java.util.TimeZone JAVA_TIME_ZONE_MISSING = new JavaMissingTimeZone();
    
    
    
    private TimeZone(int minutes) {
        // value must be within -14:00 and 14:00
        if (minutes < -14 * 60 || minutes > 14 * 60)
            throw new IllegalArgumentException();

        this.minutes = minutes;
    }
    
    public static TimeZone create(int minutes) {
        return new TimeZone(minutes);
    }

    public int hashCode() {
        return minutes;
    }
    public boolean equals(Object o) {
        return ((TimeZone)o).minutes == this.minutes;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
    
    
//
// nested inner classes
//    
    private static class ZeroTimeZone extends TimeZone {
        ZeroTimeZone() {
            super(0);
        }
        protected Object readResolve() {
            // use the singleton instance
            return ZERO;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
    
    private static class JavaZeroTimeZone extends SimpleTimeZone implements Serializable {
        JavaZeroTimeZone() {
            super(0, "XSD 'Z' timezone");
        } 
        protected Object readResolve() {
            return JAVA_TIME_ZONE_ZERO;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
    
    private static class JavaMissingTimeZone extends SimpleTimeZone implements Serializable {
        JavaMissingTimeZone() {
            super(0, "XSD missing timezone");
        } 
        protected Object readResolve() {
            return JAVA_TIME_ZONE_MISSING;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
}
