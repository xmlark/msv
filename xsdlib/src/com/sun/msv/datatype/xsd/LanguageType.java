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
 * "language" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#language for the spec
 */
public class LanguageType extends StringType
{
	/** singleton access to the plain language type */
	public static LanguageType theInstance = new LanguageType("language");
	
	public boolean verify( String content )
	{
		if(!super.verify(content))	return false;
		
		/*	RFC1766 defines the following BNF
		
			 Language-Tag = Primary-tag *( "-" Subtag )
			 Primary-tag = 1*8ALPHA
			 Subtag = 1*8ALPHA

			Whitespace is not allowed within the tag.
		*/
		
		final int len = content.length();
		int i=0; int tokenSize=0;
		
		while( i<len )
		{
			final char ch = content.charAt(i);
			if( ('a'<=ch && ch<='z') || ('A'<=ch && ch<='Z') )
			{
				tokenSize++;
				if( tokenSize==9 )
					return false;	// maximum 8 characters are allowed.
			}
			else
			if( ch=='-' )
			{
				if( tokenSize==0 )	return false;	// at least one alphabet preceeds '-'
				tokenSize=0;
			}
			else
				return false;	// invalid characters
		}
		
		if( tokenSize==0 )	return false;	// this means either string is empty or ends with '-'
		
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

		return new LanguageType(	newName,
								LengthFacet.merge(this,facets),
								PatternFacet.merge(this,facets),
								EnumerationFacet.create(this,facets),
								WhiteSpaceProcessor.create(facets),
								this );
	}
	
	/**
	 * creates a plain language type which is specified in
	 * http://www.w3.org/TR/xmlschema-2/#language
	 * 
	 * This method is only accessible within this class.
	 * To use a plain language type, use theInstance property instead.
	 */
	protected LanguageType( String typeName )
	{
		super( typeName );
	}
	
	/**
	 * constructor for derived-type from language by restriction.
	 * 
	 * To derive a datatype by restriction from language, call derive method.
	 * This method is only accessible within this class.
	 */
	protected LanguageType( String typeName, 
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration, WhiteSpaceProcessor whiteSpace,
						DataType baseType )
	{
		super( typeName, lengths, pattern, enumeration, whiteSpace, baseType );
	}
	
}
