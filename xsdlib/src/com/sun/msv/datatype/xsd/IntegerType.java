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
import java.math.BigInteger;

/**
 * "integer" type.
 * 
 * type of the value object is {@link IntegerValueType}.
 * See http://www.w3.org/TR/xmlschema-2/#integer for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class IntegerType extends IntegerDerivedType {
	
	public static final IntegerType theInstance = new IntegerType("integer");
	protected IntegerType(String typeName) { super(typeName); }
	
	public Object convertToValue( String lexicalValue, ValidationContext context ) {
		return IntegerValueType.create(lexicalValue);
	}
	
	public Object createJavaObject( String literal, ValidationContext context ) {
		Object o = convertToValue(literal,context);
		if(o==null)		return null;
		// o must be IntegerValueType.
		return new BigInteger(o.toString());
	}
	
	public Class getJavaObjectType() {
		return BigInteger.class;
	}
}
