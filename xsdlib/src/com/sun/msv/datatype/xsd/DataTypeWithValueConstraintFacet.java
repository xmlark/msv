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
 * base class for facets which constrain value space.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet
{
	DataTypeWithValueConstraintFacet(
		String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, facetName, facets );
	}
	
	final protected boolean needValueCheck() { return true; }
	
	protected final boolean checkFormat( String literal, ValidationContextProvider context )
	{
		// since we always return true for needValueCheck,
		// this method should never be called.
		throw new IllegalStateException();
	}
}
