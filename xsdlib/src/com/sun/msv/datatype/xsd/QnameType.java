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
 * "QName" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#QName for the spec
 *
 * TODO: if we have to check that prefix is actually declared,
 *       then we have to add more code here.
 */
public class QnameType extends ConcreteType
{
	public static final QnameType theInstance = new QnameType();
	private QnameType() { super("QName"); }

	protected boolean checkFormat( String content )
	{
		return XmlNames.isQualifiedName(content);
	}
	
	public Object convertToValue( String content )
	{
		if(XmlNames.isQualifiedName(content))	return content;
		else									return null;
	}
	
	public final int isFacetApplicable( String facetName )
	{
		// TODO : it seems to me that the spec has obvious typos.
		// so check it with the latest version.
		if( facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
}
