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
 * "integer" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#integer for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class IntegerType extends IntegerDerivedType
{
	public static final IntegerType theInstance = new IntegerType("integer");
	protected IntegerType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		return IntegerValueType.create(lexicalValue);
	}
}
