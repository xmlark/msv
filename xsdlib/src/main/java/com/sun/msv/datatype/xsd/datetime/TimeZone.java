/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
    /**
     * Difference from GMT in terms of minutes.
     * @deprecated here just for the serialization backward compatibility.
     */
    public int minutes;

    private Object readResolve() {
        // use java.util.TimeZone instead
        return new SimpleTimeZone(minutes*60*1000,"");
    }
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the ZERO singleton instance. Once again, using a special
     * instance is a hack to make the round-tripping work OK.
     */
    public static final java.util.TimeZone ZERO = new JavaZeroTimeZone();
    
    /**
     * The {@link java.util.TimeZone} representation that corresponds
     * to the missing time zone.
     */
    public static final java.util.TimeZone MISSING = new JavaMissingTimeZone();
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
    
    
//
// nested inner classes
//    
    /**
     * @deprecated
     *      exists just for the backward serialization compatibility.
     */
    static class ZeroTimeZone extends TimeZone {
        ZeroTimeZone() {
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
            return ZERO;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
    
    private static class JavaMissingTimeZone extends SimpleTimeZone implements Serializable {
        JavaMissingTimeZone() {
            super(0, "XSD missing timezone");
        } 
        protected Object readResolve() {
            return MISSING;
        }
        // serialization support
        private static final long serialVersionUID = 1;    
    }
}
