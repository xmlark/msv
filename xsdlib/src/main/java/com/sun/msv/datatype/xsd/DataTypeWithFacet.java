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
 * Base implementation of facet-restricted datatype
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class DataTypeWithFacet extends XSDatatypeImpl
{
    /** immediate base type, which may be a concrete type or DataTypeWithFacet */
    public final XSDatatypeImpl baseType;
    final public XSDatatype getBaseType() { return baseType; }
        
    
    /** base concrete type */
    protected final ConcreteType concreteType;
    
    /** name of this facet */
    public final String facetName;
    
    /** a flag that indicates the facet is fixed (derived types cannot specify this value anymore) */
    public final boolean isFacetFixed;
    
    /** a flag that indicates this type has value-constraint facet.
     * 
     * this value is used to cache this flag.
     */
    private final boolean needValueCheckFlag;
    
    /** constructor for facets other than WhiteSpaceFacet */
    DataTypeWithFacet( String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed )
        throws DatatypeException {
        this( nsUri, typeName, baseType, facetName, _isFixed, baseType.whiteSpace );
    }
    
    /** constructor for WhiteSpaceFacet */
    DataTypeWithFacet( String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed, WhiteSpaceProcessor whiteSpace )
        throws DatatypeException {
        super(nsUri,typeName, whiteSpace);
        this.baseType = baseType;
        this.facetName = facetName;
        this.isFacetFixed = _isFixed;
        this.concreteType = baseType.getConcreteType();
        
        needValueCheckFlag = baseType.needValueCheck();
        
        int r = baseType.isFacetApplicable(facetName);
        switch(r)
        {
        case APPLICABLE:    return;    // this facet is applicable to this type. no problem.
        case NOT_ALLOWED:
            throw new DatatypeException( localize(ERR_NOT_APPLICABLE_FACET, facetName) );
        case FIXED:
            throw new DatatypeException( localize(ERR_OVERRIDING_FIXED_FACET, facetName) );
        }
    }
    
    public boolean isContextDependent() {
        return concreteType.isContextDependent();
    }
    
    public int getIdType() {
        return concreteType.getIdType();
    }
    
    public final String displayName() {
        if( getName()!=null )   return getName();
        else                      return concreteType.getName()+"-derived";
    }
    
    public final int isFacetApplicable( String facetName ) {
        if( this.facetName.equals(facetName) ) {
            if( isFacetFixed )        return FIXED;
            else                    return APPLICABLE;
        } else
            return baseType.isFacetApplicable(facetName);
    }
    
    protected boolean needValueCheck() { return needValueCheckFlag; }
    
    final public DataTypeWithFacet getFacetObject( String facetName ) {
        if(this.facetName.equals(facetName))
            return this;
        else
            return baseType.getFacetObject(facetName);
    }
    
    final public ConcreteType getConcreteType() {
        return concreteType;
    }
    
    final public int getVariety() {
        return concreteType.getVariety();
    }
    
    final public boolean isFinal( int derivationType ) {
        return baseType.isFinal(derivationType);
    }
    
    final public String convertToLexicalValue( Object o, SerializationContext context ) {
        return concreteType.convertToLexicalValue(o,context);
    }
    final public Class getJavaObjectType() {
        return concreteType.getJavaObjectType();
    }
// DatabindableDatatype implementation
    final public Object _createJavaObject( String literal, ValidationContext context ) {
        // TODO: this can be more efficient
        if(isValid(literal,context))
            return baseType.createJavaObject(literal,context);
        else
            return null;
    }
    public String serializeJavaObject( Object value, SerializationContext context ) {
        return baseType.serializeJavaObject( value, context );
    }
    
    final protected void _checkValid(String content, ValidationContext context ) throws DatatypeException {
        // let the base type complain first.
        baseType._checkValid(content,context);
        
        // then see if the facet is satisfied.
        diagnoseByFacet(content,context);
    }
    
    protected abstract void diagnoseByFacet(String content, ValidationContext context)
        throws DatatypeException;


    // serialization support
    private static final long serialVersionUID = 1;    
}
