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
 * "int" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#int for the spec
 */
public class IntType extends IntegerDerivedType
{
	public static final IntType theInstance = new IntType("int");
	protected IntType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			lexicalValue = removeOptionalPlus(lexicalValue);
			return new Integer(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
