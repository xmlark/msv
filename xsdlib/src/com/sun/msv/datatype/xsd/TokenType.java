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

import java.util.Hashtable;


/**
 * "token" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#token for the spec
 */
public class TokenType extends NormalizedStringType
{
	/** singleton access to the plain token type */
	public static TokenType theInstance = new TokenType("token");
	
	public boolean verify( String content )
	{
		if(!super.verify(content))	return false;

		if( content.startsWith(" ") )	return false;
		if( content.endsWith(" ") )		return false;
		
		if( content.indexOf("  ")!=-1 )	return false;
		
		return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	public DataType derive( String newName, Hashtable facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.size()==0 )		return this;

		return new TokenType(	newName,
								LengthFacet.merge(this,facets),
								PatternFacet.merge(this,facets),
								EnumerationFacet.create(this,facets),
								WhiteSpaceProcessor.create(facets),
								this );
	}
	
	/**
	 * creates a plain token type which is specified in
	 * http://www.w3.org/TR/xmlschema-2/#token
	 * 
	 * This method is only accessible within this class.
	 * To use a plain token type, use theInstance property instead.
	 */
	private TokenType( String typeName )
	{
		super( typeName );
	}
	
	/**
	 * constructor for derived-type from token by restriction.
	 * 
	 * To derive a datatype by restriction from token, call derive method.
	 * This method is only accessible within this class.
	 */
	private TokenType( String typeName, 
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration, WhiteSpaceProcessor whiteSpace,
						DataType baseType )
	{
		super( typeName, lengths, pattern, enumeration, whiteSpace, baseType );
	}
	
}
