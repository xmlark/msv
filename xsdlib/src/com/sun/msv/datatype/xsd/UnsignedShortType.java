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
 * "unsignedShort" type.
 * 
 * type of the value object is <code>java.lang.Integer</code>.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedShort for the spec
 *
 * We don't have language support for unsigned datatypes, so things are not so easy.
 * UnsignedShortType uses a IntType as a base implementation, for the convenience and
 * faster performance.
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class UnsignedShortType extends IntType
{
	public static final UnsignedShortType theInstance = new UnsignedShortType();
	private UnsignedShortType() { super("unsignedShort"); }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final int upperBound = 65535;
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try
		{
			Integer v = (Integer)super.convertToValue(lexicalValue,context);
			if(v==null)						return null;
			if( v.intValue()<0 )            return null;
			if( v.intValue()>upperBound )   return null;
			return v;
		}
		catch( NumberFormatException e )
		{
			return null;
		}
	}
}
