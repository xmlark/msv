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
 * 'fractionDigits' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class FractionDigitsFacet extends DataTypeWithLexicalConstraintFacet {
    
    /** maximum number of fraction digits */
    public final int scale;

    public FractionDigitsFacet( String nsUri, String typeName, XSDatatypeImpl baseType, int _scale, boolean _isFixed )
        throws DatatypeException {
        super( nsUri, typeName, baseType, FACET_FRACTIONDIGITS, _isFixed );
        
        scale = _scale;
        
        // loosened facet check
        DataTypeWithFacet o = baseType.getFacetObject(FACET_FRACTIONDIGITS);
        if(o!=null && ((FractionDigitsFacet)o).scale < this.scale )
            throw new DatatypeException( localize( ERR_LOOSENED_FACET,
                FACET_FRACTIONDIGITS, o.displayName() ) );
        
        // consistency with precision is checked in XSDatatypeImpl.derive method.
    }

    protected boolean checkLexicalConstraint( String content ) {
        return countScale(content)<=scale;
    }
    
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        final int cnt = countScale(content);
        if(cnt<=scale)        return;
        
        throw new DatatypeException( DatatypeException.UNKNOWN, 
            localize(ERR_TOO_MUCH_SCALE,
            new Integer(cnt), new Integer(scale)) );
    }
    
    /** count the number of fractional digits.
     * 
     * this method can assume that the given literal is appropriate
     * as an decimal value.
     * 
     * "the number of fractional digits" is defined in
     * http://www.w3.org/TR/xmlschema-2/#number
     */
    final protected static int countScale( String literal ) {
        final int len = literal.length();
        boolean skipMode = true;

        int count=0;
        int trailingZero=0;
        
        for( int i=0; i<len; i++ ) {
            final char ch = literal.charAt(i);
            if( skipMode ) {
                if( ch=='.' )
                    skipMode = false;
            } else {
                if( ch=='0' )    trailingZero++;
                else            trailingZero=0;
                
                if( '0'<=ch && ch<='9' )
                    count++;
            }
        }
        
        return count-trailingZero;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
