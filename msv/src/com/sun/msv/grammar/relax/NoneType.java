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

import com.sun.msv.datatype.SerializationContext;
import com.sun.msv.datatype.xsd.ConcreteType;
import org.relaxng.datatype.ValidationContext;

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
	
	public boolean checkFormat( String literal, ValidationContext context ) {
		return false;
	}

	public Object convertToValue( String lexicalValue, ValidationContext context ) {
		return null;
	}
	
	public String convertToLexicalValue( Object o, SerializationContext context ) {
		throw new IllegalArgumentException();
	}
	
	public Class getJavaObjectType() {
		return Object.class;	// actually, it never returns a value.
	}
	
	// TODO: implement diagnoseValue
}
