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

import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;

/**
 * base class for facets which constrain value space.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet {
	
	DataTypeWithValueConstraintFacet(
		String typeName, XSDatatypeImpl baseType, String facetName, TypeIncubator facets )
		throws DatatypeException {
	
		super( typeName, baseType, facetName, facets );
	}
	
	final protected boolean needValueCheck() { return true; }
	
	protected final boolean checkFormat( String literal, ValidationContext context ) {
		return convertToValue(literal,context)!=null;
	}
}
