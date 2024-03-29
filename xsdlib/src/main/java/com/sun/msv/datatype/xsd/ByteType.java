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
 * "byte" type.
 * 
 * type of the value object is <code>java.lang.Byte</code>.
 * See http://www.w3.org/TR/xmlschema-2/#byte for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ByteType extends IntegerDerivedType {
    public final static ByteType theInstance = new ByteType();
    private ByteType() {
        super("byte",createRangeFacet(ShortType.theInstance,
            Byte.valueOf(Byte.MIN_VALUE), Byte.valueOf(Byte.MAX_VALUE)));
    }
    
    final public XSDatatype getBaseType() {
        return ShortType.theInstance;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        return load(content);
    }
    
    public static Byte load( String s ) {
        // Implementation of JDK1.2.2/JDK1.3 is suitable enough
        try {
            return Byte.valueOf(removeOptionalPlus(s));
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    public static String save( Byte v ) {
        return v.toString();
    }
    public Class getJavaObjectType() {
        return Byte.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
