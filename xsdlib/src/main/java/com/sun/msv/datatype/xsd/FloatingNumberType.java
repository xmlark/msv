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

import org.relaxng.datatype.ValidationContext;

/** base class of FloatType and DoubleType
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class FloatingNumberType extends BuiltinAtomicType implements Comparator {
    protected FloatingNumberType( String typeName ) { super(typeName); }
    
    final protected boolean checkFormat( String lexicalValue, ValidationContext context ) {
        // FloatType and DoubleType checks format by trying to convert it to value object
        return _createValue(lexicalValue,context)!=null;
    }
    
    protected static boolean isDigitOrPeriodOrSign( char ch ) {
        if( '0'<=ch && ch<='9' )    return true;
        if( ch=='+' || ch=='-' || ch=='.' )    return true;
        return false;
    }
    
    public final int compare( Object lhs, Object rhs ) {
        // float and double type has total order.
        // implementation of Float.compareTo/Double.compareTo is
        // consistent with the spec
        int r = ((Comparable)lhs).compareTo(rhs);
        if(r<0)    return -1;
        if(r>0)    return +1;
        return 0;
    }

    public final int isFacetApplicable( String facetName ) {
        // TODO : should we allow scale facet, or not?
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_MAXINCLUSIVE)
        ||    facetName.equals(FACET_MININCLUSIVE)
        ||    facetName.equals(FACET_MAXEXCLUSIVE)
        ||    facetName.equals(FACET_MINEXCLUSIVE) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    private static final long serialVersionUID = -224134863141700384L;
}
