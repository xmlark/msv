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

import com.sun.xml.util.XmlNames;

/**
 * "QName" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#QName for the spec
 *
 * TODO: if we have to check that prefix is actually declared,
 *       then we cannot derive this class from String anymore.
 */
public class QnameType extends StringType
{
	/** singleton access to the plain QName type */
	public static QnameType theInstance = new QnameType("QName");
	
	public boolean verify( String content )
	{
		if(!super.verify(content))	return false;
		
		return XmlNames.isQualifiedName(content);
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

		return new QnameType(	newName,
								LengthFacet.merge(this,facets),
								PatternFacet.merge(this,facets),
								EnumerationFacet.create(this,facets),
								WhiteSpaceProcessor.create(facets),
								this );
	}
	
	/**
	 * creates a plain QName type which is specified in
	 * http://www.w3.org/TR/xmlschema-2/#QName
	 * 
	 * This method is only accessible within this class.
	 * To use a plain QName type, use theInstance property instead.
	 */
	protected QnameType( String typeName )
	{
		super( typeName );
	}
	
	/**
	 * constructor for derived-type from QName by restriction.
	 * 
	 * To derive a datatype by restriction from QName, call derive method.
	 * This method is only accessible within this class.
	 */
	protected QnameType( String typeName, 
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration, WhiteSpaceProcessor whiteSpace,
						DataType baseType )
	{
		super( typeName, lengths, pattern, enumeration, whiteSpace, baseType );
	}
	
}
