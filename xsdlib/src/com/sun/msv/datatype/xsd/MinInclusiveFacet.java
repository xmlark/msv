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
 * 'minInclusive' facet
 * 
 * @author	Kohsuke Kawaguchi
 */
public class MinInclusiveFacet extends RangeFacet
{
	protected MinInclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MININCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.LESS || r==Comparator.EQUAL;
	}
}
