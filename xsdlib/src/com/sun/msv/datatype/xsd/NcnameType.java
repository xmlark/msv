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

import com.sun.xml.util.XmlNames;

/**
 * "NCName" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#NCName for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NcnameType extends TokenType
{
	public static final NcnameType theInstance = new NcnameType();
	private NcnameType() { super("NCName"); }
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		if(XmlNames.isNCNmtoken(content))	return content;
		else								return null;
	}
}
