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

import java.math.BigInteger;
import org.relaxng.datatype.ValidationContext;

/**
 * "byte" type.
 * 
 * type of the value object is <code>java.lang.Byte</code>.
 * See http://www.w3.org/TR/xmlschema-2/#byte for the spec
 * 
 * @author	Kohsuke Kawaguchi
 */
public class ByteType extends IntegerDerivedType {
	public final static ByteType theInstance = new ByteType();
	private ByteType() { super("byte"); }
	
	public Object convertToValue( String content, ValidationContext context ) {
		// Implementation of JDK1.2.2/JDK1.3 is suitable enough
		try {
			content = removeOptionalPlus(content);
			return new Byte(content);
		} catch( NumberFormatException e ) {
			return null;
		}
	}
	public Class getJavaObjectType() {
		return Byte.class;
	}
}
