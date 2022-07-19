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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * "length", "minLength", and "maxLength" facet validator.
 * 
 * this class also detects inconsistent facet setting
 * (for example, minLength=100 and maxLength=0)
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LengthFacet extends DataTypeWithValueConstraintFacet {
    public final int length;
    
    protected LengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
        throws DatatypeException {
        this(nsUri,typeName,baseType,
            facets.getNonNegativeInteger(FACET_LENGTH),
            facets.isFixed(FACET_LENGTH));
    }
                
    protected LengthFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _length, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,FACET_LENGTH,_isFixed);
    
        length = _length;
        
        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_LENGTH);
        if(o!=null && ((LengthFacet)o).length != this.length )
            throw new DatatypeException(
                localize(ERR_LOOSENED_FACET, FACET_LENGTH, o.displayName() ) );
        
        // consistency with minLength/maxLength is checked in XSDatatypeImpl.derive method.
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        Object o = baseType._createValue(content,context);
        if(o==null || ((Discrete)concreteType).countLength(o)!=length)    return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        Object o = concreteType._createValue(content,context);
        // base type must have accepted this lexical value, otherwise 
        // this method is never called.
        if(o==null)    throw new IllegalStateException();    // assertion
        
        int cnt = ((Discrete)concreteType).countLength(o);
        if(cnt!=length)
            throw new DatatypeException( DatatypeException.UNKNOWN,
                localize(ERR_LENGTH, new Integer(cnt), new Integer(length)) );
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
