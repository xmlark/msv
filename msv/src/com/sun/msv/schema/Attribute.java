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
package com.sun.tranquilo.schema;

import java.util.Hashtable;

import org.xml.sax.Attributes;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.DataTypeFactory;

/**
 * Validation unit that corresponds with an XML attribute
 * 
 * this class is shared between Module and Grammar.
 */
public class Attribute
{
	/** verifies XML attribute set
	 * 
	 * @param domesticValidation
	 *		"true" indicates that validation should be done for local,
	 *		unprefixed attribute. "false" indicates that it should be done
	 *		for exported, prefixed attribute.
	 * 
	 * @return true		if there is no validity violation
	 */
	public boolean verify( Attributes a, boolean domesticValidation )
	{
		final String ns = domesticValidation?"":this.namespace;
		final int idx = a.getIndex( ns, localName );
	
		if(idx==-1)
		{// attribute is not found in the set
			// if the attribute is required, validation should be failed
			return !isRequired;
		}
		
		// attribute exists, now checks validity of its value.
		return type.verify( a.getValue(idx) );
	}
	
	public ValidationErrorDiagnosis diagnose( Attributes a, boolean domesticValidation )
	{
		final String ns = domesticValidation?"":this.namespace;
		final int idx = a.getIndex( ns, localName );
		
		// pseudo qualified name that will be used to denote this attribute
		String qName ="";
		if( ns.length()!=0 )
			qName = "{" + namespace +"}:";
		qName += localName;
		// TODO : use prefix if it's available
						
		
		if( idx==-1 )
		{
			if( isRequired )
			{
				return new ValidationErrorDiagnosis(
					ValidationErrorDiagnosis.MSG_Attribute_RequiredAttributeIsMissing,
					qName );
			}
			return null;		// not an error
		}
		
		if( type.verify(a.getValue(idx)) )		return null;	// not an error
		
		return new ValidationErrorDiagnosis(
			type.diagnose( a.getValue(idx) ),
			ValidationErrorDiagnosis.MSG_Attribute_AttributeValueIsInvalid,
			qName );
	}
	
	// TODO : write wrapper functions for all
	
	/** namespace URI to which this object belongs */
	public String namespace;
	/** name of the attribute */
	public String localName;
	/** a flag that indicates whether this attribute is mandatory or not */
	public boolean isRequired;
	/** datatype object that validates value of the attribute */
	public DataType type;
	
//	/** parent schema object to which this object belongs */
//	public final Schema parentSchema;
	
	/**
	 * construct object
	 * 
	 * @param schema
	 *		parent schma object under which this object is used.
	 */
	public Attribute( String namespaceUri, String localName, boolean required, DataType type )
	{
		this.namespace	= namespaceUri;
		this.localName	= localName;
		this.isRequired	= required;
		this.type		= type;
	}
	
}
