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
 * base class for facets which constrains lexical space of data
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class DataTypeWithLexicalConstraintFacet extends DataTypeWithFacet
{
	DataTypeWithLexicalConstraintFacet(
		String typeName, DataTypeImpl baseType, String facetName, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, facetName, facets );
	}
	
	// this class does not perform any lexical check.
	protected final boolean checkFormat( String literal, ValidationContextProvider context )
	{
		if(!baseType.checkFormat(literal,context))	return false;
		return checkLexicalConstraint(literal);
	}
	
	public final Object convertToValue( String literal, ValidationContextProvider context )
	{
		Object o = baseType.convertToValue(literal,context);
		if(o!=null && !checkLexicalConstraint(literal) )	return null;
		return o;
	}

	protected abstract boolean checkLexicalConstraint( String literal );
}
