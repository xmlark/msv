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

import org.relaxng.datatype.ValidationContext;

/**
 * "Name" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#Name for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NameType extends TokenType {
	public static final NameType theInstance = new NameType();
	private NameType() { super("Name"); }
	
	public Object convertToValue( String content, ValidationContext context ) {
		if(XmlNames.isName(content))	return content;
		else							return null;
	}
}
