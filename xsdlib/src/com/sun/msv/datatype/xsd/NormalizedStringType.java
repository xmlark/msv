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

/**
 * "normalizedString" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#normalizedString for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NormalizedStringType extends StringType {
	public static final NormalizedStringType theInstance = new NormalizedStringType("normalizedString");
	protected NormalizedStringType(String typeName) {
		super(typeName, WhiteSpaceProcessor.theReplace);
	}
	
	public XSDatatype getBaseType() {
		return StringType.theInstance;
	}
}
