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
import com.sun.msv.datatype.xsd.datetime.BigTimeDurationValueType;
import com.sun.msv.datatype.xsd.datetime.ITimeDurationValueType;
import org.relaxng.datatype.ValidationContext;

/**
 * "duration" type.
 * 
 * type of the value object is {@link ITimeDurationValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#duration for the spec
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class DurationType extends BuiltinAtomicType implements Comparator {
    
    public static final DurationType theInstance = new DurationType();
    private DurationType() { super("duration"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    protected boolean checkFormat( String content, ValidationContext context ) {
        try {
            new BigTimeDurationValueType(content);
            return true;
        } catch( IllegalArgumentException e ) {
            return false;
        }
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        try {
            return new BigTimeDurationValueType(content);
        } catch( IllegalArgumentException e ) {
            return null;
        }
    }
    public Class getJavaObjectType() {
        return ITimeDurationValueType.class;
    }
    
    /** compare two TimeDurationValueType */
    public int compare( Object lhs, Object rhs ) {
        return ((ITimeDurationValueType)lhs).compare((ITimeDurationValueType)rhs);
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_MAXINCLUSIVE)
        ||    facetName.equals(FACET_MAXEXCLUSIVE)
        ||    facetName.equals(FACET_MININCLUSIVE)
        ||    facetName.equals(FACET_MINEXCLUSIVE) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }
    
    public String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof ITimeDurationValueType))
            throw new IllegalArgumentException();
        
        return value.toString();
    }

    
    
    
    // serialization support
    private static final long serialVersionUID = 1;
}

