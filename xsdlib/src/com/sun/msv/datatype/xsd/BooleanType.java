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
 * "boolean" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#boolean for the spec
 */
public class BooleanType extends ConcreteType
{
	public static final BooleanType theInstance = new BooleanType();
	
	private BooleanType()	{ super("boolean"); }
	
	protected boolean checkFormat( String content, ValidationContextProvider context )
	{
		return "true".equals(content) || "false".equals(content);
	}
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{// for string, lexical space is value space by itself
		if( lexicalValue.equals("true") )		return Boolean.TRUE;
		if( lexicalValue.equals("false") )		return Boolean.FALSE;
		return null;
	}
	
	public int isFacetApplicable( String facetName )
	{
		if(facetName.equals("pattern"))		return APPLICABLE;
		return NOT_ALLOWED;
	}
}
