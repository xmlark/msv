/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/**
 * Base implementation of facet-restricted datatype
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DataTypeWithFacet extends DataTypeImpl
{
	/** immediate base type, which may be a concrete type or DataTypeWithFacet */
	protected final DataTypeImpl baseType;
	
	/** base concrete type */
	protected final ConcreteType concreteType;
	
	/** name of this facet */
	protected final String facetName;
	
	/** a flag that indicates the facet is fixed (derived types cannot specify this value anymore) */
	protected final boolean isFacetFixed;
	
	/** a flag that indicates this type has value-constraint facet.
	 * 
	 * this value is used to cache this flag.
	 */
	private final boolean needValueCheckFlag;
	
	/** constructor for facets other than WhiteSpaceFacet */
	DataTypeWithFacet( String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException
	{
		this( typeName, baseType, facetName, facets, baseType.whiteSpace );
	}
	
	/** constructor for WhiteSpaceFacet */
	DataTypeWithFacet( String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets, WhiteSpaceProcessor whiteSpace )
		throws BadTypeException
	{
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
	
	
	public final String displayName()
	{
		return concreteType.getName()+"-derived";
	}
	
	public final int isFacetApplicable( String facetName )
	{
		if( this.facetName.equals(facetName) )
		{
			if( isFacetFixed )		return FIXED;
			else					return APPLICABLE;
		}
		else
			return baseType.isFacetApplicable(facetName);
	}
	
	protected boolean needValueCheck() { return needValueCheckFlag; }
	
	final protected DataTypeWithFacet getFacetObject( String facetName )
	{
		if(this.facetName.equals(facetName))
			return this;
		else
			return baseType.getFacetObject(facetName);
	}
	
	final protected ConcreteType getConcreteType()
	{
		return concreteType;
	}
	
	final public boolean isAtomType()
	{
		return concreteType.isAtomType();
	}
	
	final public boolean isFinal( int derivationType )
	{
		return baseType.isFinal(derivationType);
	}
	
	final protected DataTypeErrorDiagnosis diagnoseValue(String content, ValidationContextProvider context )
	{
		// if base type complains, pass it through.
		DataTypeErrorDiagnosis err = baseType.diagnoseValue(content,context);
		if(err!=null)		return err;
		
		// otherwise, perform own diagnosis.
		return diagnoseByFacet(content,context);
	}
	
	protected abstract DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
		throws UnsupportedOperationException;

}
