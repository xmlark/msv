/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.*;
import com.sun.msv.datatype.SerializationContext;

/**
 * Base implementation of facet-restricted datatype
 * 
 * @author	Kohsuke Kawaguchi
 */
public abstract class DataTypeWithFacet extends XSDatatypeImpl
{
	/** immediate base type, which may be a concrete type or DataTypeWithFacet */
	public final XSDatatypeImpl baseType;
	
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
	DataTypeWithFacet( String typeName, XSDatatypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException {
		this( typeName, baseType, facetName, facets, baseType.whiteSpace );
	}
	
	/** constructor for WhiteSpaceFacet */
	DataTypeWithFacet( String typeName, XSDatatypeImpl baseType, String facetName, TypeIncubator facets, WhiteSpaceProcessor whiteSpace )
		throws BadTypeException {
		super(typeName, whiteSpace);
		this.baseType = baseType;
		this.facetName = facetName;
		this.isFacetFixed = facets.isFixed(facetName);
		this.concreteType = baseType.getConcreteType();
		
		needValueCheckFlag = baseType.needValueCheck();
		
		int r = baseType.isFacetApplicable(facetName);
		switch(r)
		{
		case APPLICABLE:	return;	// this facet is applicable to this type. no problem.
		case NOT_ALLOWED:
			throw new BadTypeException( BadTypeException.ERR_NOT_APPLICABLE_FACET, facetName );
		case FIXED:
			throw new BadTypeException( BadTypeException.ERR_OVERRIDING_FIXED_FACET, facetName );
		}
	}
	
	
	public final String displayName() {
		return concreteType.getName()+"-derived";
	}
	
	public final int isFacetApplicable( String facetName ) {
		if( this.facetName.equals(facetName) ) {
			if( isFacetFixed )		return FIXED;
			else					return APPLICABLE;
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
	
	final public boolean isAtomType() {
		return concreteType.isAtomType();
	}
	
	final public boolean isFinal( int derivationType ) {
		return baseType.isFinal(derivationType);
	}
	
	final public String convertToLexicalValue( Object o, SerializationContext context ) {
		return concreteType.convertToLexicalValue(o,context);
	}
	public Class getJavaObjectType() {
		return concreteType.getJavaObjectType();
	}
	
	final protected void diagnoseValue(String content, ValidationContext context ) throws DatatypeException {
		// let the base type complain first.
		baseType.diagnoseValue(content,context);
		
		// then see if the facet is satisfied.
		diagnoseByFacet(content,context);
	}
	
	protected abstract void diagnoseByFacet(String content, ValidationContext context)
		throws DatatypeException;

}
