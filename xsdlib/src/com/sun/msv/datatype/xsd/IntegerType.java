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
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IntegerType extends IntegerDerivedType {
	
	public static final IntegerType theInstance = new IntegerType("integer");
	protected IntegerType(String typeName) { super(typeName); }
	
	public XSDatatype getBaseType() {
		return NumberType.theInstance;
	}
	
	public Object _createValue( String lexicalValue, ValidationContext context ) {
		return IntegerValueType.create(lexicalValue);
	}
	
	public Object _createJavaObject( String literal, ValidationContext context ) {
        return load(literal);
	}
    
    public BigInteger load( String s ) {
		Object o = _createValue(s,null);
		if(o==null)		return null;
		// o must be IntegerValueType.
		return new BigInteger(o.toString());
    }
    public String save( BigInteger v ) {
        return v.toString();
    }
	
	// the default implementation of the serializeJavaObject method works correctly.
	// so there is no need to override it here.
	
	public Class getJavaObjectType() {
		return BigInteger.class;
	}
}
