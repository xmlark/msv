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
 * union type.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class UnionType extends ConcreteType {
    /**
     * derives a new datatype from atomic datatypes by union
     */
    public UnionType( String nsUri, String newTypeName, XSDatatype[] memberTypes ) throws DatatypeException {
        super(nsUri,newTypeName);

        if(memberTypes.length==0)
            throw new DatatypeException(localize(ERR_EMPTY_UNION));
        
        XSDatatypeImpl[] m = new XSDatatypeImpl[memberTypes.length];
        System.arraycopy( memberTypes, 0, m, 0, memberTypes.length );
        
        for( int i=0; i<m.length; i++ )
            if( m[i].isFinal(DERIVATION_BY_UNION) )
                throw new DatatypeException(localize(
                    ERR_INVALID_MEMBER_TYPE, m[i].displayName() ));
        
        this.memberTypes = m;
    }
    
    /** member types */
    final public XSDatatypeImpl[] memberTypes;

    
    final public XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }
    
    
    public final String displayName() {
        String name = getName();
        if(name!=null)      return name;
        else                return "union";
    }

    /**
     * The union type is context-dependent if one of the member types is so.
     */
    public boolean isContextDependent() {
        for( int i=0; i<memberTypes.length; i++ )
            if(memberTypes[i].isContextDependent())
                return true;
        return false;
    }
    
    /*
        TODO: the getIdType method of the union type.
    
        What is the return code of the getIdType if the type is an union
        of ID and Short?
    
        For this reason, for now, the getIdType method follows its default
        implementation in the XSDatatypeImpl class, which is ID_TYPE_NULL.
    */
    
    /**
     * Variety of the UnionType is VARIETY_UNION. So this method always
     * returns VARIETY_UNION.
     */
    public final int getVariety() {
        return VARIETY_UNION;
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( facetName.equals(FACET_PATTERN)
        ||    facetName.equals(FACET_ENUMERATION) )
            return APPLICABLE;
        else
            return NOT_ALLOWED;
    }
    
    protected final boolean checkFormat( String content, ValidationContext context ) {
        for( int i=0; i<memberTypes.length; i++ )
            if( memberTypes[i].checkFormat(content,context) )    return true;
        
        return false;
    }
    
    public Object _createValue( String content, ValidationContext context ) {
        Object o;
        for( int i=0; i<memberTypes.length; i++ ) {
            o = memberTypes[i]._createValue(content,context);
            if(o!=null)        return o;
        }
        
        return null;
    }
    public Class getJavaObjectType() {
        // TODO: find the common base type, if it's possible.
        return Object.class;
    }
    
    public String convertToLexicalValue( Object o, SerializationContext context ) {
        for( int i=0; i<memberTypes.length; i++ ) {
            try {
                return memberTypes[i].convertToLexicalValue(o,context);
            } catch( Exception e ) {
                ;    // ignore
            }
        }
        
        throw new IllegalArgumentException();
    }
    
    protected void _checkValid(String content, ValidationContext context) throws DatatypeException {
        // what is the appropriate implementation for union?
        if( checkFormat(content,context) )        return;
        else    throw new DatatypeException();
    }


    // serialization support
    private static final long serialVersionUID = 1;    
}
