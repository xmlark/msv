/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.datatype;

/**
 * "string" and string-derived types
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
		// TODO : should we allow scale facet, or not?
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
