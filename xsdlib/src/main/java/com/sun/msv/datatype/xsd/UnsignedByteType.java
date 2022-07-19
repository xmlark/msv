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

package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;

/**
 * "unsignedByte" type.
 * 
 * type of the value object is <code>java.lang.Short</code>.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedByte for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnsignedByteType extends ShortType {
    public static final UnsignedByteType theInstance = new UnsignedByteType();
    private UnsignedByteType() {
        super("unsignedByte",createRangeFacet(
            UnsignedShortType.theInstance,
            null,
            new Short((short)255)));
    }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final short upperBound = 255;
    
    final public XSDatatype getBaseType() {
        return UnsignedShortType.theInstance;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            Short v = (Short)super._createValue(lexicalValue,context);
            if( v==null )                        return null;
            if( v.shortValue()<0 )               return null;
            if( v.shortValue()>upperBound )      return null;
            return v;
        } catch( NumberFormatException e ) {
            return null;
        }
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
