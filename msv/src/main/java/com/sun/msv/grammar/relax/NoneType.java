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

package com.sun.msv.grammar.relax;

import org.relaxng.datatype.ValidationContext;

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.BuiltinAtomicType;
import com.sun.msv.datatype.xsd.SimpleURType;
import com.sun.msv.datatype.xsd.XSDatatype;

/**
 * 'none' datatype of RELAX.
 * 
 * this type accepts nothing.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NoneType extends BuiltinAtomicType {
    
    public static final NoneType theInstance = new NoneType();
    private NoneType() { super("none"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    public int isFacetApplicable( String facetName ) {
        return NOT_ALLOWED;
    }
    
    public boolean checkFormat( String literal, ValidationContext context ) {
        return false;
    }

    public Object _createValue( String lexicalValue, ValidationContext context ) {
        return null;
    }
    
    public String convertToLexicalValue( Object o, SerializationContext context ) {
        throw new IllegalArgumentException();
    }
    
    public Class getJavaObjectType() {
        return Object.class;    // actually, it never returns a value.
    }
    
    // TODO: implement _checkValid
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
