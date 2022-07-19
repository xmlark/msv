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
 * "boolean" type.
 * 
 * type of the value object is <code>java.lang.Boolean</code>.
 * See http://www.w3.org/TR/xmlschema-2/#boolean for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BooleanType extends BuiltinAtomicType {
    public static final BooleanType theInstance = new BooleanType();
    
    private BooleanType()    { super("boolean"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    protected boolean checkFormat( String content, ValidationContext context ) {
        return "true".equals(content) || "false".equals(content)
            || "0".equals(content) || "1".equals(content);
    }
    
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        // for string, lexical space is value space by itself
        return load(lexicalValue);
    }
    
    public static Boolean load( String s ) {
        if( s.equals("true") )        return Boolean.TRUE;
        if( s.equals("1") )            return Boolean.TRUE;
        if( s.equals("0") )            return Boolean.FALSE;
        if( s.equals("false") )        return Boolean.FALSE;
        return null;
    }

    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if( value instanceof Boolean )
            return save( (Boolean)value );
        else
            throw new IllegalArgumentException();
    }

    public static String save( Boolean b ) {
        if( b.booleanValue()==true )    return "true";
        else                            return "false";
    }
    
    public int isFacetApplicable( String facetName ) {
        if(facetName.equals(FACET_PATTERN)
        || facetName.equals(FACET_ENUMERATION)
        || facetName.equals(FACET_WHITESPACE))
            return APPLICABLE;
        return NOT_ALLOWED;
    }
    public Class getJavaObjectType() {
        return Boolean.class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
