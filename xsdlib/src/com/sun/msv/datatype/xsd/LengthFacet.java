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

import java.util.Hashtable;

/**
 * "length", "minLength", and "maxLength" facet validator.
 * 
 * this class also detects inconsistent facet setting
 * (for example, minLength=100 and maxLength=0)
 * 
 * @author	Kohsuke Kawaguchi
 */
public class LengthFacet extends DataTypeWithValueConstraintFacet
{
	private final int length;
	
	protected LengthFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super(typeName,baseType,FACET_LENGTH,facets);
	
		length = facets.getNonNegativeInteger(FACET_LENGTH);
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_LENGTH);
		if(o!=null && ((LengthFacet)o).length != this.length )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_LENGTH, o.getName() );
		
		// consistency with minLength/maxLength is checked in DataTypeImpl.derive method.
	}
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		Object o = baseType.convertToValue(content,context);
		if(o==null || ((Discrete)concreteType).countLength(o)!=length)	return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		Object o = concreteType.convertToValue(content,context);
		// base type must have accepted this lexical value, otherwise 
		// this method is never called.
		if(o==null)	throw new IllegalStateException();	// assertion
		
		int cnt = ((Discrete)concreteType).countLength(o);
		if(cnt!=length)
			return new DataTypeErrorDiagnosis( this, content, -1,
				localize(ERR_LENGTH, new Integer(cnt), new Integer(length)) );
		
		return null;
	}
}
