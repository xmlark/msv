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

package com.sun.msv.datatype.xsd.ngimpl;

import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.datatype.xsd.XSDatatypeImpl;
import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.DatatypeBuilder;
import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

class DatatypeBuilderImpl implements DatatypeBuilder {
    
    private TypeIncubator incubator;
        
    DatatypeBuilderImpl( XSDatatype baseType ) {
        incubator = new TypeIncubator(baseType);
    }
    
    public void addParameter( String name, String value, ValidationContext context )
                    throws DatatypeException {
        
        if( name.equals(XSDatatype.FACET_ENUMERATION) )
            // the enumeration facet is not allowed for RELAX NG.
            throw new DatatypeException(
                XSDatatypeImpl.localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, name) );
            
        
        incubator.addFacet( name, value, false, context );
        
        if( name.equals(XSDatatype.FACET_PATTERN) )
            // if the pattern facet is specified, we have to
            // derive a new type so that multiple pattern facets can
            // work as ANDing.
            incubator = new TypeIncubator(incubator.derive(null,null));
    }
    
    public Datatype createDatatype() throws DatatypeException {
        return incubator.derive(null,null);
    }
}
