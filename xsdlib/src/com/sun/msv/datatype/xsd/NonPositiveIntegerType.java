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
 * "nonPositiveInteger" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#nonPositiveInteger for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NonPositiveIntegerType extends IntegerType
{
	public static final NonPositiveIntegerType theInstance = new NonPositiveIntegerType();
	private NonPositiveIntegerType() { super("nonPositiveInteger"); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		Object o = super.convertToValue(lexicalValue,context);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isNonPositive() )	return null;
		return v;
	}
}
