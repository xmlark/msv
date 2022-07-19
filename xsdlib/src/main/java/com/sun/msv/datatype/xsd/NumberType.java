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
import org.relaxng.datatype.ValidationContext;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * "decimal" type.
 * 
 * type of the value object is <code>java.math.BigDecimal</code>.
 * See http://www.w3.org/TR/xmlschema-2/#decimal for the spec.
 * It was once known as "number" type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NumberType extends BuiltinAtomicType implements Comparator {
    public static final NumberType theInstance = new NumberType();
    private NumberType() { super("decimal"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    /** constant */
    private static final BigInteger the10 = new BigInteger("10");

    protected boolean checkFormat( String content, ValidationContext context ) {
        final int len = content.length();
        int i=0;
        char ch;
        boolean atLeastOneDigit = false;
        
        if(len==0)    return false;        // length 0 is not allowed
        
        // leading optional sign
        ch = content.charAt(0);
        if(ch=='-' || ch=='+')    i++;
        
        while(i<len) {
            ch = content.charAt(i++);
            if('0'<=ch && ch<='9') {
                atLeastOneDigit = true;
                continue;
            }
            if(ch=='.')    break;
            return false;        // other characters are error
        }
        
        while(i<len) {
            // fractional part
            ch = content.charAt(i++);
            if('0'<=ch && ch<='9') {
                atLeastOneDigit = true;
                continue;
            }
            return false;    // other characters are error
        }
        
        return atLeastOneDigit;    // at least one digit must be present.
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        // BigDecimal accepts expressions like "1E4",
        // but XML Schema doesn't.
            
        // so call checkFormat to make sure that
        // format is XML Schema spec compliant.
        if(!checkFormat(content,context))        return null;

        return load(content);
    }
    
    public static BigDecimal load( String content ) {
        try    {
            // XML Schema allows optional leading '+' sign,
            // but BigDecimal doesn't.
            // so remove it here.
            
            if( content.length()==0 )        return null;
            
            if( content.charAt(0)=='+' )
                content = content.substring(1);
            
            BigDecimal r = new BigDecimal(content);
            
            // BigDecimal treats 0 != 0.0
            // to workaround this, "normalize" BigDecimal;
            // that is, trailing zeros in fractional digits are removed.
            while(r.scale()>0) {
                BigInteger[] q_r = 
                    r.unscaledValue().divideAndRemainder(the10);
                
                if( !q_r[1].equals(BigInteger.ZERO) )    break;
                
                r = new BigDecimal(q_r[0], r.scale()-1);
            }
            
            return r;
        } catch( NumberFormatException e ) {
            return null;
        }
    }
    
    public static String save( Object o ) {
        return ((BigDecimal)o).toString();
    }
    
    public Class getJavaObjectType() {
        return BigDecimal.class;
    }
    
    public String convertToLexicalValue( Object o, SerializationContext context ) {
        if(o instanceof BigDecimal)
            return o.toString();
        else
            throw new IllegalArgumentException();
    }

    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_TOTALDIGITS)
        ||    facetName.equals(FACET_FRACTIONDIGITS)
        ||    facetName.equals(FACET_PATTERN)
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

    public final int compare( Object o1, Object o2 ) {
        final int r = ((Comparable)o1).compareTo(o2);
        if(r<0)    return LESS;
        if(r>0)    return GREATER;
        return EQUAL;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
