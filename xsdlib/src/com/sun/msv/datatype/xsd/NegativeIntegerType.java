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
 * "negativeInteger" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#negativeInteger for the spec
 * 
 * v.isNegative is certainly faster than compareTo(ZERO).
 * This the sole reason why this class exists at all.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NegativeIntegerType extends IntegerType
{
	public static final NegativeIntegerType theInstance = new NegativeIntegerType();
	private NegativeIntegerType() { super("negativeInteger"); }
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		Object o = super.convertToValue(lexicalValue,context);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isNegative() )	return null;
		return v;
	}
}
