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
 * "normalizedString" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#normalizedString for the spec
 */
public class NormalizedStringType extends StringType
{
	/** singleton access to the plain normalizedString type */
	public static NormalizedStringType theInstance = new NormalizedStringType("normalizedString");
	
	public boolean verify( String content )
	{
		if(!super.verify(content))	return false;
		
		final int len = content.length();
		for( int i=0; i<len; i++ )
		{
			final char ch = content.charAt(i);
			
			if( ch==0x09 || ch==0x0A || ch==0x0D )
				return false;	// tab, CR, LF are not allowed.
		}
		
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

		return new NormalizedStringType(	newName,
								LengthFacet.merge(this,facets),
								PatternFacet.merge(this,facets),
								EnumerationFacet.create(this,facets),
								WhiteSpaceProcessor.create(facets),
								this );
	}
	
	/**
	 * creates a plain normalizedString type which is specified in
	 * http://www.w3.org/TR/xmlschema-2/#normalizedString
	 * 
	 * This method is only accessible within this class.
	 * To use a plain normalizedString type, use theInstance property instead.
	 */
	protected NormalizedStringType(String typeName)
	{
		super( typeName );
	}
	
	/**
	 * constructor for derived-type from normalizedString by restriction.
	 * 
	 * To derive a datatype by restriction from normalizedString, call derive method.
	 * This method is only accessible within this class.
	 */
	protected NormalizedStringType(String typeName,LengthFacet lengths,PatternFacet pattern,EnumerationFacet enumeration,WhiteSpaceProcessor whiteSpace,DataType baseType)
	{
		super( typeName, lengths, pattern, enumeration, whiteSpace, baseType );
	}
	
}
