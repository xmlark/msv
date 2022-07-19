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

import com.sun.msv.datatype.SerializationContext;
import org.relaxng.datatype.ValidationContext;

/**
 * "float" type.
 * 
 * type of the value object is <code>java.lang.Float</code>.
 * See http://www.w3.org/TR/xmlschema-2/#float for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FloatType extends FloatingNumberType {
    
    public static final FloatType theInstance = new FloatType();
    private FloatType() { super("float"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return load(lexicalValue);
    }
    
    public static Float load( String s ) {
        
        /* Incompatibilities of XML Schema's float "xfloat" and Java's float "jfloat"
        
            * jfloat.valueOf ignores leading and trailing whitespaces,
              whereas this is not allowed in xfloat.
            * jfloat.valueOf allows "float type suffix" (f, F) to be
              appended after float literal (e.g., 1.52e-2f), whereare
              this is not the case of xfloat.
        
            gray zone
            ---------
            * jfloat allows ".523". And there is no clear statement that mentions
              this case in xfloat. Although probably this is allowed.
            * 
        */
        
        try {
            if(s.equals("NaN"))        return new Float(Float.NaN);
            if(s.equals("INF"))        return new Float(Float.POSITIVE_INFINITY);
            if(s.equals("-INF"))    return new Float(Float.NEGATIVE_INFINITY);
            
            if(s.length()==0
            || !isDigitOrPeriodOrSign(s.charAt(0))
            || !isDigitOrPeriodOrSign(s.charAt(s.length()-1)) )
                return null;
            
            // these screening process is necessary due to the wobble of Float.valueOf method
            return Float.valueOf(s);
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    public Class getJavaObjectType() {
        return Float.class;
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof Float ))
            throw new IllegalArgumentException();
        
        return save( (Float)value );
    }
    
    public static String save( Float value ) {
        float v = value.floatValue();
        if (Float.isNaN(v)) return "NaN";
        if (v==Float.POSITIVE_INFINITY) return "INF";
        if (v==Float.NEGATIVE_INFINITY) return "-INF";
        return value.toString();
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
