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

import org.relaxng.datatype.DatatypeException;
import org.relaxng.datatype.ValidationContext;

/**
 * Base class of "(max|min)(In|Ex)clusive" facet validator
 * 
 * @author Kohsuke KAWAGUCHI
 */
public abstract class RangeFacet extends DataTypeWithValueConstraintFacet {
	public final Object limitValue;

	protected RangeFacet( String typeName, XSDatatypeImpl baseType, String facetName, TypeIncubator facets )
		throws DatatypeException {
		super(typeName,baseType,facetName,facets);
		limitValue = facets.getFacet(facetName);
	}
	
	public final Object convertToValue( String literal, ValidationContext context ) {
		Object o = baseType.convertToValue(literal,context);
		if(o==null)	return null;
		
		int r = ((Comparator)concreteType).compare(limitValue,o);
		if(!rangeCheck(r))		return null;
		return o;
	}
	
	protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
		if( convertToValue(content,context)!=null )		return;
			
		throw new DatatypeException( DatatypeException.UNKNOWN,
			localize(ERR_OUT_OF_RANGE, facetName, limitValue) );
	}
	
	protected abstract boolean rangeCheck( int compareResult );
}
