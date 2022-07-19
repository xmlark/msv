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
 * "double" type.
 * 
 * type of the value object is <code>java.lang.Double</code>.
 * See http://www.w3.org/TR/xmlschema-2/#double for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DoubleType extends FloatingNumberType {
    
    public static final DoubleType theInstance = new DoubleType();
    private DoubleType() { super("double"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return load(lexicalValue);
    }
    
    public static Double load( String lexicalValue ) {
        // TODO : probably the same problems exist as in the case of float
        try {
            if(lexicalValue.equals("NaN"))    return new Double(Double.NaN);
            if(lexicalValue.equals("INF"))    return new Double(Double.POSITIVE_INFINITY);
            if(lexicalValue.equals("-INF"))    return new Double(Double.NEGATIVE_INFINITY);
            
            if(lexicalValue.length()==0
            || !isDigitOrPeriodOrSign(lexicalValue.charAt(0))
            || !isDigitOrPeriodOrSign(lexicalValue.charAt(lexicalValue.length()-1)) )
                return null;
            
            
            // these screening process is necessary due to the wobble of Float.valueOf method
            return Double.valueOf(lexicalValue);
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof Double ))
            throw new IllegalArgumentException();
        
        return save((Double)value);
    }
    
    public static String save( Double value ) {
        double v = value.doubleValue();
        if (Double.isNaN(v)) return "NaN";
        if (v==Double.POSITIVE_INFINITY) return "INF";
        if (v==Double.NEGATIVE_INFINITY) return "-INF";
        return value.toString();
    }
    public Class getJavaObjectType() {
        return Double.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
