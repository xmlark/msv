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

/**
 * "QName" type.
 * 
 * type of the value object is {@link QnameValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#QName for the spec.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class QnameType extends BuiltinAtomicType implements Discrete {
    public static final QnameType theInstance = new QnameType();
    private QnameType() { super("QName"); }
    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    /**
     * QName type always returns true. That is, QName is a context-dependent type.
     */
    public boolean isContextDependent() {
        return true;
    }
    
    protected boolean checkFormat( String value, ValidationContext context ) {
        // [6] QName ::= (Prefix ':')? LocalPart
        // [7] Prefix ::= NCName
        // [8] LocalPart ::= NCName

        final int first = value.indexOf(':');

        // no Prefix, only check LocalPart
        if(first <= 0)        return XmlNames.isUnqualifiedName(value);

        // Prefix exists, check everything
        final int    last = value.lastIndexOf(':');
        if (last != first)    return false;

        final String prefix = value.substring (0, first);
        return XmlNames.isUnqualifiedName(prefix)
            && XmlNames.isUnqualifiedName(value.substring (first + 1))
            && context.resolveNamespacePrefix(prefix)!=null;
    }
    
    public Object _createValue( String value, ValidationContext context ) {
        String uri,localPart;
        // [6] QName ::= (Prefix ':')? LocalPart
        // [7] Prefix ::= NCName
        // [8] LocalPart ::= NCName

        final int first = value.indexOf(':');

        if(first <= 0)
        {// no Prefix, only check LocalPart
            if(!XmlNames.isUnqualifiedName(value))    return null;
            uri = context.resolveNamespacePrefix("");
            localPart = value;
        } else {
            // Prefix exists, check everything
            final int    last = value.lastIndexOf (':');
            if (last != first)    return null;
            
            final String prefix = value.substring(0, first);
            localPart = value.substring(first + 1);
            
            if(!XmlNames.isUnqualifiedName(prefix)
            || !XmlNames.isUnqualifiedName(localPart) )
                return null;
            
            uri = context.resolveNamespacePrefix(prefix);
        }
        
        if(uri==null)    return null;
        
        return new QnameValueType(uri,localPart);
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION)
        ||  facetName.equals(FACET_WHITESPACE)
        ||    facetName.equals(FACET_LENGTH)
        ||    facetName.equals(FACET_MAXLENGTH)
        ||    facetName.equals(FACET_MINLENGTH)
        )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }
    
    public final int countLength( Object value ) {
        QnameValueType v = (QnameValueType)value;
        
        // the spec does not define what is the unit of length.
        // TODO: check the update of the spec and modify this if necessary.
        return    UnicodeUtil.countLength( v.namespaceURI )+
                UnicodeUtil.countLength( v.localPart );
    }

    
    public String convertToLexicalValue( Object o, SerializationContext context ) {
        if(!( o instanceof QnameValueType ))
            throw new UnsupportedOperationException();
        
        QnameValueType v = (QnameValueType)o;
        
        return serialize(v.namespaceURI,v.localPart,context);
    }

    public String serializeJavaObject( Object value, SerializationContext context ) {
        if(!(value instanceof String[]))    throw new IllegalArgumentException();
        String[] input = (String[])value;
        if( input.length!=2 )                throw new IllegalArgumentException();
        
        return serialize(input[0],input[1],context);
    }
    
    private String serialize( String uri, String local, SerializationContext context ) {
        String prefix = context.getNamespacePrefix(uri);
        if(prefix==null)    return local;
        else                return prefix+":"+local;
    }
    
    public Object _createJavaObject( String literal, ValidationContext context ) {
        QnameValueType v = (QnameValueType)createValue(literal,context);
        if(v==null)        return null;
        // return String[2]
        else            return new String[]{v.namespaceURI,v.localPart};
    }

    public Class getJavaObjectType() {
        return String[].class;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
