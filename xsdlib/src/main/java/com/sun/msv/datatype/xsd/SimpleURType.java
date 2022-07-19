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
 * simple "ur-type" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-1/#simple-ur-type-itself for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleURType extends BuiltinAtomicType {
    
    public static final SimpleURType theInstance = new SimpleURType();
    
    protected SimpleURType() {
        super("anySimpleType",WhiteSpaceProcessor.thePreserve);
    }
    
    /**
     * SimpleURType always returns null to indicate that 
     * there is no base type for this type.
     */
    final public XSDatatype getBaseType() {
        return null;
    }
    
    /**
     * simple ur-type accepts anything.
     */
    protected final boolean checkFormat( String content, ValidationContext context ) {
        return true;
    }
    
    /**
     * the value object of the simple ur-type is the lexical value itself.
     */
    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return lexicalValue;
    }
    public Class getJavaObjectType() {
        return String.class;
    }

    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if( value instanceof String )
            return (String)value;
        else
            throw new IllegalArgumentException();
    }
    
    /**
     * no facet is applicable to the simple ur-type.
     */
    public final int isFacetApplicable( String facetName ) {
        return APPLICABLE;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
