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
 * "unsignedByte" type.
 * 
 * type of the value object is <code>java.lang.Short</code>.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedByte for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class UnsignedByteType extends ShortType {
	public static final UnsignedByteType theInstance = new UnsignedByteType();
	private UnsignedByteType() { super("unsignedByte"); }

    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final short upperBound = 255;

	public Object convertToValue( String lexicalValue, ValidationContextProvider context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try {
			Short v = (Short)super.convertToValue(lexicalValue,context);
			if( v==null )						return null;
			if( v.shortValue()<0 )               return null;
			if( v.shortValue()>upperBound )      return null;
			return v;
		} catch( NumberFormatException e ) {
			return null;
		}
	}
}
