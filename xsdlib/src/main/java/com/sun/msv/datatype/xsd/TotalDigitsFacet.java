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
 * 'totalDigits' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TotalDigitsFacet extends DataTypeWithLexicalConstraintFacet {
    /** maximum number of total digits. */
    public final int        precision;

    public TotalDigitsFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _precision, boolean _isFixed )
        throws DatatypeException {
        super( nsUri, typeName, baseType, FACET_TOTALDIGITS, _isFixed );
        
        precision = _precision;
        
        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_TOTALDIGITS);
        if(o!=null && ((TotalDigitsFacet)o).precision < this.precision )
            throw new DatatypeException( localize( ERR_LOOSENED_FACET,
                FACET_TOTALDIGITS, o.displayName() ) );
        
        // consistency with scale is checked in XSDatatypeImpl.derive method.
    }

    protected boolean checkLexicalConstraint( String content ) {
        return countPrecision(content)<=precision;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        final int cnt = countPrecision(content);
        if( cnt<=precision )    return;
        
        throw new DatatypeException( DatatypeException.UNKNOWN,
            localize(ERR_TOO_MUCH_PRECISION, Integer.valueOf(cnt), Integer.valueOf(precision)) );
    }
    
    /** counts the number of digits */
    protected static int countPrecision( String literal ) {
        int start = 0;
        if(literal.length()>0 && (literal.charAt(0)=='+' || literal.charAt(0)=='-'))
            start = 1;

        int dot = literal.indexOf('.');
        int intEnd = (dot<0) ? literal.length() : dot;
        int fracStart = (dot<0) ? literal.length() : dot+1;
        int fracEnd = literal.length();

        // Integer trailing zeros are significant. Only trim leading zeros.
        int intStart = start;
        while( intStart<intEnd && literal.charAt(intStart)=='0' )
            intStart++;
        int intDigits = intEnd - intStart;

        // Fraction leading zeros are significant, but trailing zeros are not.
        while( fracEnd>fracStart && literal.charAt(fracEnd-1)=='0' )
            fracEnd--;
        int fracDigits = fracEnd - fracStart;

        int total = intDigits + fracDigits;
        // Zero still has precision 1.
        return (total==0) ? 1 : total;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
