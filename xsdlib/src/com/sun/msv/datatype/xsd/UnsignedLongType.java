/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.xsd;

import org.relaxng.datatype.ValidationContext;

/**
 * "unsignedLong" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#unsignedLong for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class UnsignedLongType extends IntegerType {
	public static final UnsignedLongType theInstance = new UnsignedLongType();
	private UnsignedLongType() { super("unsignedLong"); }
	
	final public XSDatatype getBaseType() {
		return NonNegativeIntegerType.theInstance;
	}

    /** upper bound value. this is the maximum possible valid value as an unsigned long */
    private static final IntegerValueType upperBound
		= IntegerValueType.create("18446744073709551615");
	
	public Object convertToValue( String lexicalValue, ValidationContext context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		final IntegerValueType v = IntegerValueType.create(lexicalValue);
		if(v==null)							return null;
		if( !v.isNonNegative() )            return null;
		if( upperBound.compareTo(v)<0 )     return null;
		return v;
	}
}
