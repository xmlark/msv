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
 * "Name" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#Name for the spec
 * 
 * @author Kohsuke KAWAGUCHI
 */
public class NameType extends TokenType
{
	public static final NameType theInstance = new NameType();
	private NameType() { super("Name"); }
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
		if(XmlNames.isName(content))	return content;
		else							return null;
	}
}
