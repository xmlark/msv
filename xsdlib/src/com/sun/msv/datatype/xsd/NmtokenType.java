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
 * "NMTOKEN" type.
 * 
 * type of the value object is <code>java.lang.String</code>.
 * See http://www.w3.org/TR/xmlschema-2/#NMTOKEN for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NmtokenType extends TokenType {
	public static final NmtokenType theInstance = new NmtokenType("NMTOKEN");
	protected NmtokenType(String typeName) { super(typeName); }
	
	public Object convertToValue( String content, ValidationContext context ) {
		if(XmlNames.isNmtoken(content))		return content;
		else								return null;
	}
}
