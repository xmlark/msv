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
 * "short" type.
 * 
 * type of the value object is <code>java.lang.Short</code>.
 * See http://www.w3.org/TR/xmlschema-2/#short for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class ShortType extends IntegerDerivedType {
	public static final ShortType theInstance = new ShortType("short");
	protected ShortType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try {
			lexicalValue = removeOptionalPlus(lexicalValue);
			return new Short(lexicalValue);
		} catch( NumberFormatException e ) {
			return null;
		}
	}
}
