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

import org.relaxng.datatype.ValidationContext;

/**
 * "long" type.
 * 
 * type of the value object is <code>java.lang.Long</code>.
 * See http://www.w3.org/TR/xmlschema-2/#long for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class LongType extends IntegerDerivedType {
	public static final LongType theInstance = new LongType();
	private LongType() { super("long"); }
	protected LongType( String typeName ) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContext context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try {
			lexicalValue = removeOptionalPlus(lexicalValue);
			return new Long(lexicalValue);
		} catch( NumberFormatException e ) {
			return null;
		}
	}
	public Class getJavaObjectType() {
		return Long.class;
	}
}
