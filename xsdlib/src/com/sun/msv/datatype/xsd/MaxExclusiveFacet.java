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

/**
 * 'maxExclusive' facet
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MaxExclusiveFacet extends RangeFacet {
	protected MaxExclusiveFacet( String typeName, XSDatatypeImpl baseType, TypeIncubator facets )
		throws DatatypeException {
		super( typeName, baseType, FACET_MAXEXCLUSIVE, facets );
	}
	
	protected final boolean rangeCheck( int r ) {
		return r==Comparator.GREATER;
	}
}
