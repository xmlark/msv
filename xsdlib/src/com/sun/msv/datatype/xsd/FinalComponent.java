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
 * "final" component.
 * 
 * @author	Kohsuke Kawaguchi
 */
public final class FinalComponent extends DataTypeImpl
{
	/** immediate base type, which may be a concrete type or DataTypeWithFacet */
	protected final DataTypeImpl baseType;
	
	protected final int finalValue;
	
	public boolean isAtomType() { return baseType.isAtomType(); }
	
	public FinalComponent( DataTypeImpl baseType, int finalValue )
	{
		super( baseType.getName(), baseType.whiteSpace );
		this.baseType = baseType;
		this.finalValue = finalValue;
	}
	
	public boolean isFinal( int derivationType )
	{
		if( (finalValue&derivationType) != 0 )	return true;
		return baseType.isFinal(derivationType);
	}
	
	public ConcreteType getConcreteType()
	{
		return baseType.getConcreteType();
	}
	public String displayName()
	{
		return baseType.displayName();
	}
	public int isFacetApplicable( String facetName )
	{
		return baseType.isFacetApplicable(facetName);
	}
	public boolean checkFormat( String content, ValidationContextProvider context )
	{
		return baseType.checkFormat(content,context);
	}
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		return baseType.convertToValue(content,context);
	}
	public DataTypeErrorDiagnosis diagnoseValue( String content, ValidationContextProvider context )
	{
		return baseType.diagnoseValue(content,context);
	}
	
}
