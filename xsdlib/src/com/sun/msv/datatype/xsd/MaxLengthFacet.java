/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

/**
 * 'maxLength' facet
 * 
 * @author	Kohsuke Kawaguchi
 */
public class MaxLengthFacet extends DataTypeWithValueConstraintFacet
{
	public final int maxLength;
	
	protected MaxLengthFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException {
		super(typeName,baseType,FACET_MAXLENGTH,facets);
	
		maxLength = facets.getNonNegativeInteger(FACET_MAXLENGTH);

		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_MAXLENGTH);
		if(o!=null && ((MaxLengthFacet)o).maxLength < this.maxLength )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_MAXLENGTH, o.displayName() );
		
		// consistency with minLength is checked in DataTypeImpl.derive method.
	}
	
	public Object convertToValue( String literal, ValidationContextProvider context ) {
		Object o = baseType.convertToValue(literal,context);
		if(o==null || ((Discrete)concreteType).countLength(o)>maxLength)	return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context) {
		Object o = concreteType.convertToValue(content,context);
		// base type must have accepted this lexical value, otherwise 
		// this method is never called.
		if(o==null)	throw new IllegalStateException();	// assertion
		
		int cnt = ((Discrete)concreteType).countLength(o);
		if(cnt>maxLength)
			return new DataTypeErrorDiagnosis( this, content, -1,
				localize(ERR_MAXLENGTH, new Integer(cnt), new Integer(maxLength)) );
		
		return null;
	}
}
