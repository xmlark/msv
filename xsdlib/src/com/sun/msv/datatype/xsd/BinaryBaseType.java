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

/**
 * base implementation for "hexBinary" and "base64Binary" types.
 * 
 * @author	Kohsuke Kawaguchi
 */
abstract class BinaryBaseType extends ConcreteType implements Discrete {
	BinaryBaseType( String typeName ) { super(typeName); }
	
	final public int isFacetApplicable( String facetName ) {
		if( facetName.equals( FACET_LENGTH )
		||	facetName.equals( FACET_MAXLENGTH )
		||	facetName.equals( FACET_MINLENGTH )
		||	facetName.equals( FACET_PATTERN )
		||	facetName.equals( FACET_ENUMERATION ) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	final public int countLength( Object value ) {
		// for binary types, length is the number of bytes
		return ((BinaryValueType)value).rawData.length;
	}
}
