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
 *
 * $Id$
 */
package com.sun.tranquilo.datatype;

import com.sun.xml.util.XmlNames;

/**
 * "QName" and string-derived types.
 * 
 * See http://www.w3.org/TR/xmlschema-2/#QName for the spec
 */
public class QnameType extends ConcreteType
{
	public static final QnameType theInstance = new QnameType();
	private QnameType() { super("QName"); }

	private static String getNamespaceURI( String content, ValidationContextProvider context )
	{
		return context.resolveNamespacePrefix( content.substring(0, content.indexOf(':')) );
	}
	
	protected boolean checkFormat( String value, ValidationContextProvider context )
	{
		// [6] QName ::= (Prefix ':')? LocalPart
		// [7] Prefix ::= NCName
		// [8] LocalPart ::= NCName

		final int first = value.indexOf(':');

		// no Prefix, only check LocalPart
		if(first <= 0)		return XmlNames.isUnqualifiedName(value);

		// Prefix exists, check everything
		final int	last = value.lastIndexOf(':');
		if (last != first)	return false;

		final String prefix = value.substring (0, first);
		return XmlNames.isUnqualifiedName(prefix)
			&& XmlNames.isUnqualifiedName(value.substring (first + 1))
			&& context.resolveNamespacePrefix(prefix)!=null;
	}
	
	public Object convertToValue( String value, ValidationContextProvider context )
	{
		String uri,localPart;
		// [6] QName ::= (Prefix ':')? LocalPart
		// [7] Prefix ::= NCName
		// [8] LocalPart ::= NCName

		final int first = value.indexOf(':');

		if(first <= 0)
		{// no Prefix, only check LocalPart
			if(!XmlNames.isUnqualifiedName(value))	return null;
			uri = context.resolveNamespacePrefix("");
			localPart = value;
		}
		else
		{// Prefix exists, check everything
			final int	last = value.lastIndexOf (':');
			if (last != first)	return null;
			
			final String prefix = value.substring(0, first);
			localPart = value.substring(first + 1);
			
			if(!XmlNames.isUnqualifiedName(prefix)
			|| !XmlNames.isUnqualifiedName(localPart) )
				return null;
			
			uri = context.resolveNamespacePrefix(prefix);
		}
		
		if(uri==null)	return null;
		
		return new QnameValueType(uri,localPart);
	}
	
	public final int isFacetApplicable( String facetName )
	{
		// TODO : it seems to me that the spec has obvious typos.
		// cause range-related facets does not make sense at all.
		// so check it with the latest version.
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION)
// TODO : what is "length" of QName? number of characters?
//		||	facetName.equals(FACET_LENGTH)
//		||	facetName.equals(FACET_MAXLENGTH)
//		||	facetName.equals(FACET_MINLENGTH)
		)
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
}
