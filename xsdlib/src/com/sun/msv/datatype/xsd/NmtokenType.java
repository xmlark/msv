/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/**
 * "NMTOKEN" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#NMTOKEN for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NmtokenType extends TokenType
{
	public static final NmtokenType theInstance = new NmtokenType("NMTOKEN");
	protected NmtokenType(String typeName) { super(typeName); }
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		if(XmlNames.isNmtoken(content))		return content;
		else								return null;
	}
}
