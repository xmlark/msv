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
 * Base class of "(max|min)(In|Ex)clusive" facet validator
 * 
 * @author Kohsuke KAWAGUCHI
 */
abstract class RangeFacet extends DataTypeWithValueConstraintFacet
{
	protected final Object limitValue;

	protected RangeFacet( String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException
	{
		super(typeName,baseType,facetName,facets);
		limitValue = facets.getFacet(facetName);
	}
	
	public final Object convertToValue( String literal, ValidationContextProvider context )
	{
		Object o = baseType.convertToValue(literal,context);
		if(o==null)	return null;
		
		int r = ((Comparator)concreteType).compare(limitValue,o);
		if(!rangeCheck(r))		return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		if( convertToValue(content,context)!=null )		return null;
			
		return new DataTypeErrorDiagnosis(this, content, -1,
			localize(ERR_OUT_OF_RANGE, facetName, limitValue) );
	}
	
	
	protected abstract boolean rangeCheck( int compareResult );
}
