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
 * "positiveInteger" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#positiveInteger for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class PositiveIntegerType extends IntegerType {
	
	public static final PositiveIntegerType theInstance = new PositiveIntegerType();
	private PositiveIntegerType() { super("positiveInteger"); }
	
	final public XSDatatype getBaseType() {
		return NonNegativeIntegerType.theInstance;
	}
	
	public Object convertToValue( String lexicalValue, ValidationContext context ) {
		Object o = super.convertToValue(lexicalValue,context);
		if(o==null)		return null;
		
		final IntegerValueType v = (IntegerValueType)o;
		if( !v.isPositive() )	return null;
		return v;
	}
}
