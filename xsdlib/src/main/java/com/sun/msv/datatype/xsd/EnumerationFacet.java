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

import java.util.Collection;
import java.util.Set;

/**
 * "enumeration" facets validator.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EnumerationFacet extends DataTypeWithValueConstraintFacet {
    protected EnumerationFacet( String nsUri, String typeName, XSDatatypeImpl baseType, Collection _values, boolean _isFixed )
        throws DatatypeException {
        super(nsUri,typeName,baseType,FACET_ENUMERATION,_isFixed);
        values = new java.util.HashSet( _values );
    }
    
    /** set of valid values */
    public final Set values;

    public Object _createValue( String literal, ValidationContext context ) {
        Object o = baseType._createValue(literal,context);
        if(o==null || !values.contains(o))        return null;
        return o;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if( _createValue(content,context)!=null )    return;
        
        // TODO: guess which item the user was trying to specify
        
        if( values.size()<=4 ) {
            // if choices are small in number, include them into error messages.
            Object[] members = values.toArray();
            String r="";
            
            if( members[0] instanceof String
            ||  members[0] instanceof Number ) {
                // this will cover 80% of the use case.
                r += "\""+members[0].toString()+"\"";
                for( int i=1; i<members.length; i++ )
                    r+= "/\""+members[i].toString()+"\"";
                
                r = "("+r+")";    // oh, don't tell me I should use StringBuffer.
                
                throw new DatatypeException( DatatypeException.UNKNOWN,
                    localize(ERR_ENUMERATION_WITH_ARG, r) );
            }
        }
        throw new DatatypeException( DatatypeException.UNKNOWN,
            localize(ERR_ENUMERATION) );
    }


    // serialization support
    private static final long serialVersionUID = 1;    
}
