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
 * "language" and string-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#language for the spec
 */
public class LanguageType extends TokenType
{
	public static final LanguageType theInstance = new LanguageType();
	private LanguageType() { super("language"); }
	
	public Object convertToValue( String content, ValidationContextProvider context )
	{
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
					return null;	// maximum 8 characters are allowed.
			}
			else
			if( ch=='-' )
			{
				if( tokenSize==0 )	return null;	// at least one alphabet preceeds '-'
				tokenSize=0;
			}
			else
				return null;	// invalid characters
		}
		
		if( tokenSize==0 )	return null;	// this means either string is empty or ends with '-'
		
		return content;
	}
}
