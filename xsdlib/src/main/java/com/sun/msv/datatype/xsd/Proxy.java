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
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * Delegates all methods to the base type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Proxy extends XSDatatypeImpl {
    /** immediate base type, which may be a concrete type or DataTypeWithFacet */
    public final XSDatatypeImpl baseType;
    final public XSDatatype getBaseType() { return baseType; }
    
    public Proxy( String nsUri, String newTypeName, XSDatatypeImpl baseType ) {
        super( nsUri, newTypeName, baseType.whiteSpace );
        this.baseType = baseType;
    }
    
    public boolean isContextDependent() {
        return baseType.isContextDependent();
    }
    
    public int getIdType() {
        return baseType.getIdType();
    }
    
    public boolean isFinal( int derivationType ) {
        return baseType.isFinal(derivationType);
    }
    
    public ConcreteType getConcreteType() {
        return baseType.getConcreteType();
    }
    
    public String displayName() {
        return baseType.displayName();
    }
    
    public int getVariety() {
        return baseType.getVariety();
    }
    
    public int isFacetApplicable( String facetName ) {
        return baseType.isFacetApplicable(facetName);
    }
    
    public boolean checkFormat( String content, ValidationContext context ) {
        return baseType.checkFormat(content,context);
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        return baseType._createValue(content,context);
    }

    public DataTypeWithFacet getFacetObject( String facetName ) {
        return baseType.getFacetObject(facetName);
    }
    
    public Class getJavaObjectType() {
        return baseType.getJavaObjectType();
    }
    
    public Object _createJavaObject( String literal, ValidationContext context ) {
        return baseType._createJavaObject(literal,context);
    }
    
    public String serializeJavaObject( Object value, SerializationContext context ) {
        return baseType.serializeJavaObject(value,context);
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        return baseType.convertToLexicalValue(value,context);
    }
    
    public void _checkValid( String content, ValidationContext context ) throws DatatypeException {
        baseType._checkValid(content,context);
    }
    

    // serialization support
    private static final long serialVersionUID = 1;    
}
