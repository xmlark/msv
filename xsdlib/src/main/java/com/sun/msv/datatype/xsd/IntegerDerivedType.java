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
 * base class for types derived from integer.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class IntegerDerivedType extends BuiltinAtomicType implements Comparator {

    private final XSDatatypeImpl baseFacets;
    
    
    protected IntegerDerivedType( String typeName, XSDatatypeImpl _baseFacets ) {
        super(typeName);
        this.baseFacets = _baseFacets;
    }
    
    public final int isFacetApplicable( String facetName ) {
        // TODO : should we allow scale facet, or not?
        if( facetName.equals(FACET_TOTALDIGITS)
        ||    facetName.equals(FACET_PATTERN)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_ENUMERATION)
        ||    facetName.equals(FACET_MAXINCLUSIVE)
        ||    facetName.equals(FACET_MININCLUSIVE)
        ||    facetName.equals(FACET_MAXEXCLUSIVE)
        ||    facetName.equals(FACET_MINEXCLUSIVE) )
            return APPLICABLE;
        
        if( facetName.equals(FACET_FRACTIONDIGITS) )
            return FIXED;
            
       return NOT_ALLOWED;
    }

    public DataTypeWithFacet getFacetObject(String facetName) {
        return baseFacets.getFacetObject(facetName);
    }
    
    protected final boolean checkFormat( String content, ValidationContext context ) {
        // integer-derived types always checks lexical format by trying to convert it to value object
        return _createValue(content,context)!=null;
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if( value instanceof Number || value instanceof IntegerValueType )
            return value.toString();
        else
            throw new IllegalArgumentException("invalid value type:"+value.getClass().toString());
    }
    
    public final int compare( Object o1, Object o2 ) {
        // integer-derived type always uses Comparable object as its value type
        final int r = ((Comparable)o1).compareTo(o2);
        if(r<0)    return LESS;
        if(r>0)    return GREATER;
        return EQUAL;
    }

    /**
     * removes leading optional '+' sign.
     * 
     * Several Java conversion functions (e.g., Long.parseLong)
     * do not accept leading '+' sign.
     */
    protected static String removeOptionalPlus(String s) {
        if(s.length()<=1 || s.charAt(0)!='+')    return s;
        
        s = s.substring(1);
        char ch = s.charAt(0);
        if('0'<=ch && ch<='9')    return s;
        if('.'==ch )    return s;
        
        throw new NumberFormatException();
    }
    
    /** Apply a range facet. */
    protected static XSDatatypeImpl createRangeFacet( XSDatatypeImpl baseType, Number min, Number max ) {
         try {
             XSDatatypeImpl r = baseType;
             if( min!=null )
                r = new MinInclusiveFacet(null,null,r,min,false);
             if( max!=null )
                r = new MaxInclusiveFacet(null,null,r,max,false);
             return r;
         } catch( DatatypeException e ) {
             throw new InternalError(); // impossible
         }
    }
    
    
    // serialization support
    private static final long serialVersionUID = -7353993842821534786L;    
}
