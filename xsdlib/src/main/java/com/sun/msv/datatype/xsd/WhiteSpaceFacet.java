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
 * whiteSpace facet validator
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class WhiteSpaceFacet extends DataTypeWithFacet {
    
    WhiteSpaceFacet( String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
        throws DatatypeException {
        
        this(nsUri,typeName, baseType,
            WhiteSpaceProcessor.get( (String)facets.getFacet(FACET_WHITESPACE)),
            facets.isFixed(FACET_WHITESPACE) );
    }
        
    WhiteSpaceFacet( String nsUri, String typeName, XSDatatypeImpl baseType,
        WhiteSpaceProcessor proc, boolean _isFixed ) throws DatatypeException {
            
        super(nsUri,typeName, baseType, FACET_WHITESPACE, _isFixed,proc);
        
        // loosened facet check
        if( baseType.whiteSpace.tightness() > this.whiteSpace.tightness() ) {
            XSDatatype d;
            d=baseType.getFacetObject(FACET_WHITESPACE);
            if(d==null)    d = getConcreteType();
            
            throw new DatatypeException( localize(
                ERR_LOOSENED_FACET,    FACET_WHITESPACE, d.displayName() ));
        }
        
        // consistency with minLength/maxLength is checked in XSDatatypeImpl.derive method.
    }
    
    protected boolean checkFormat( String content, ValidationContext context ) {
        return baseType.checkFormat(content,context);
    }
    public Object _createValue( String content, ValidationContext context ) {
        return baseType._createValue(content,context);
    }
    
    /** whiteSpace facet never constrain anything */
    protected void diagnoseByFacet(String content, ValidationContext context) {
        ;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
