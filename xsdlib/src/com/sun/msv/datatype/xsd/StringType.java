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
 * "string" type.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#string for the spec
 */
public class StringType extends ConcreteType implements Discrete
{
	public static final StringType theInstance
		= new StringType("string",WhiteSpaceProcessor.thePreserve);
	
	protected StringType( String typeName, WhiteSpaceProcessor whiteSpace )
	{ super(typeName,whiteSpace); }
	
	protected final boolean checkFormat( String content, ValidationContextProvider context )
	{// string derived types should use convertToValue method to check its validity
		return convertToValue(content,context)!=null;
	}
	
	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{// for string, lexical space is value space by itself
		return lexicalValue;
	}
	
	public final int countLength( Object value )
	{// for string-derived types, length means number of XML characters.
		return UnicodeUtil.countLength( (String)value );
	}
	
	public final int isFacetApplicable( String facetName )
	{
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
		||	facetName.equals(FACET_WHITESPACE)
		||	facetName.equals(FACET_LENGTH)
		||	facetName.equals(FACET_MAXLENGTH)
		||	facetName.equals(FACET_MINLENGTH) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
}
