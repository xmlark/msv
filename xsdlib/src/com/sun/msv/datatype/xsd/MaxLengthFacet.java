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
 * 'maxLength' facet
 * 
 * @author	Kohsuke Kawaguchi
 */
public class MaxLengthFacet extends DataTypeWithValueConstraintFacet
{
	public final int maxLength;
	
	protected MaxLengthFacet( String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
		throws DatatypeException {
		super(typeName,baseType,FACET_MAXLENGTH,facets);
	
		maxLength = facets.getNonNegativeInteger(FACET_MAXLENGTH);

		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_MAXLENGTH);
		if(o!=null && ((MaxLengthFacet)o).maxLength < this.maxLength )
			throw new DatatypeException( localize( ERR_LOOSENED_FACET,
				FACET_MAXLENGTH, o.displayName() ) );
		
		// consistency with minLength is checked in XSDatatypeImpl.derive method.
	}
	
	public Object convertToValue( String literal, ValidationContext context ) {
		Object o = baseType.convertToValue(literal,context);
		if(o==null || ((Discrete)concreteType).countLength(o)>maxLength)	return null;
		return o;
	}
	
	protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
		Object o = concreteType.convertToValue(content,context);
		// base type must have accepted this lexical value, otherwise 
		// this method is never called.
		if(o==null)	throw new IllegalStateException();	// assertion
		
		int cnt = ((Discrete)concreteType).countLength(o);
		if(cnt>maxLength)
			throw new DatatypeException( DatatypeException.UNKNOWN,
				localize(ERR_MAXLENGTH, new Integer(cnt), new Integer(maxLength)) );
	}
}
