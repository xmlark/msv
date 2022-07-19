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
import com.sun.msv.datatype.xsd.datetime.CalendarFormatter;
import com.sun.msv.datatype.xsd.datetime.CalendarParser;
import com.sun.msv.datatype.xsd.datetime.IDateTimeValueType;
import com.sun.msv.datatype.xsd.datetime.PreciseCalendarFormatter;
import com.sun.msv.datatype.xsd.datetime.PreciseCalendarParser;
import org.relaxng.datatype.ValidationContext;

import java.util.Calendar;

/**
 * base implementation of dateTime and dateTime-truncated types.
 * this class uses IDateTimeValueType as the value object.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class DateTimeBaseType extends BuiltinAtomicType implements Comparator {
    
    protected DateTimeBaseType(String typeName) {
        super(typeName);
    }

    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    protected final boolean checkFormat(String content, ValidationContext context) {
        // string derived types should use _createValue method to check its validity
        try {
            CalendarParser.parse(getFormat(),content);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public final Object _createValue(String content, ValidationContext context) {
        // for string, lexical space is value space by itself
        try {
            return PreciseCalendarParser.parse(getFormat(),content);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public final String convertToLexicalValue( Object value, SerializationContext context ) {
        if(!(value instanceof IDateTimeValueType))
            throw new IllegalArgumentException();
        
        return PreciseCalendarFormatter.format(getFormat(),(IDateTimeValueType)value);
        
    }
        
    /** converts our DateTimeValueType to a java-friendly Date type. */
    public final Object _createJavaObject(String literal, ValidationContext context) {
        return CalendarParser.parse(getFormat(),literal);
    }

    public final String serializeJavaObject(Object value, SerializationContext context) {
        if(!(value instanceof Calendar))    throw new IllegalArgumentException();
        
        return CalendarFormatter.format( getFormat(), (Calendar)value );
    }
    
    public Class getJavaObjectType() {
        return Calendar.class;
    }
    
    /**
     * Formatting string passed to {@link CalendarParser#parse(String, String)}.
     */
    protected abstract String getFormat();
    
    
    
    

    /** compare two DateTimeValueType */
    public int compare(Object lhs, Object rhs) {
        return ((IDateTimeValueType)lhs).compare((IDateTimeValueType)rhs);
    }
    
    public final int isFacetApplicable(String facetName) {
        if(facetName.equals(FACET_PATTERN)
        || facetName.equals(FACET_ENUMERATION)
        || facetName.equals(FACET_WHITESPACE)
        || facetName.equals(FACET_MAXINCLUSIVE)
        || facetName.equals(FACET_MAXEXCLUSIVE)
        || facetName.equals(FACET_MININCLUSIVE)
        || facetName.equals(FACET_MINEXCLUSIVE))
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }

    private static final long serialVersionUID = 1465669066779112677L;
}
