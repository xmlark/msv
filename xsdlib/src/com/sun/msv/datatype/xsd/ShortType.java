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
 * "short" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#short for the spec
 */
public class ShortType extends IntegerDerivedType
{
	public static final ShortType theInstance = new ShortType();
	private ShortType() { super("short"); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			return new Short(lexicalValue);
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
