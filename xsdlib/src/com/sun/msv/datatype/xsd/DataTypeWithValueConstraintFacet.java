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

import org.relaxng.datatype.ValidationContext;

/**
 * base class for facets which constrain value space.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet {
	
	DataTypeWithValueConstraintFacet(
		String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException {
	
		super( typeName, baseType, facetName, facets );
	}
	
	final protected boolean needValueCheck() { return true; }
	
	protected final boolean checkFormat( String literal, ValidationContext context ) {
		return convertToValue(literal,context)!=null;
	}
}
