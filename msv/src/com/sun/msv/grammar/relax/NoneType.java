/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relax;

import com.sun.msv.datatype.*;

/**
 * 'none' datatype of RELAX.
 * 
 * this type accepts nothing.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NoneType extends ConcreteType {
	
	public static final NoneType theInstance = new NoneType();
	private NoneType() { super("none"); }
	
	public int isFacetApplicable( String facetName ) {
		return NOT_ALLOWED;
	}
	
	public boolean checkFormat( String literal, ValidationContextProvider context ) {
		return false;
	}

	public Object convertToValue( String lexicalValue, ValidationContextProvider context ) {
		return null;
	}
	
	public String convertToLexicalValue( Object o ) {
		throw new IllegalArgumentException();
	}
	
	// TODO: implement diagnoseValue
}
