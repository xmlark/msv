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
 * 'maxInclusive' facet
 * 
 * @author	Kohsuke Kawaguchi
 */
public class MaxInclusiveFacet extends RangeFacet
{
	protected MaxInclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MAXINCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.GREATER || r==Comparator.EQUAL;
	}
}
