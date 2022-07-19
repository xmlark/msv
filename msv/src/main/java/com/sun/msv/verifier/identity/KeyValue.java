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

package com.sun.msv.verifier.identity;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

/**
 * represents multi-field keys.
 * 
 * this class implements equality test and hash code based on
 * the equalities of each item.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class KeyValue {
    public final Object[] values;
    
    /** source location that this value is found. */
    public final Locator locator;
    
    KeyValue( Object[] values, Locator loc ) {
        this.values = values;
        if(loc==null)   this.locator = null;
        else            this.locator = new LocatorImpl(loc);
    }
    
    public int hashCode() {
        int code = 0;
        for( int i=0; i<values.length; i++ )
            code ^= values[i].hashCode();
        return code;
    }
    
    public boolean equals( Object o ) {
        if(!(o instanceof KeyValue))    return false;
        KeyValue rhs = (KeyValue)o;
        if( values.length!=rhs.values.length )    return false;
        
        for( int i=0; i<values.length; i++ )
            if( !values[i].equals(rhs.values[i]) )    return false;
        
        return true;
    }
}
