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
 * Comparable datatype.
 *
 * Those datatypes which has order relation must implement this interface.
 * RangeFacet uses this interface to do its job.
 * 
 * @author	Kohsuke Kawaguchi
 */
public interface Comparator
{
	static final int LESS			= -1;	// lhs < rhs
	static final int EQUAL			= 0;	// lhs = rhs
	static final int GREATER		= 1;	// lhs > rhs
	static final int UNDECIDABLE	= 999;	// lhs ? rhs
	
	/**
	 * compare to value types and decides its order relation
	 */
	int compare( Object o1, Object o2 );
}
