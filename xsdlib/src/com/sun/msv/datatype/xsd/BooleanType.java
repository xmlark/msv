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
 * "boolean" and boolean-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#boolean for the spec
 */
public class BooleanType extends ConcreteType
{
	public static final BooleanType theInstance = new BooleanType();
	
	private BooleanType()	{ super("boolean"); }
	
	protected boolean checkFormat( String content )
	{
		return "true".equals(content) || "false".equals(content);
	}
	
	public Object convertToValue( String lexicalValue )
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
