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

import org.xml.sax.Attributes;
import org.xml.sax.Locator;

/**
 * pseudo XML element
 */
public class XMLElement
{
	/** localPart of the element name */
	public final String		tagName;
	public final Attributes	attributes;
	public final Locator	locator;
	/** prefix-qualified name of the element */
	public final String		qName;
	
	public XMLElement( String tagName, String qName, Attributes attributes, Locator locator )
	{
		this.tagName		= tagName;
		this.qName			= qName;
		this.attributes		= attributes;
		this.locator		= locator;
	}
	
	/** examines if the element has specified attribute */
	public boolean hasAttribute( String localName )
	{
		return attributes.getIndex("",localName)!=-1;
	}
	
	/** gets the value of attribute */
	public String getAttribute( String localName )
	{
		return attributes.getValue("",localName);
	}
	
	/**
	 * reads attribute value from an element.
	 * If the specified attribute is not present, throws an exception
	 */
	protected String getRequiredAttribute( String attributeName )
		throws SchemaParseException
	{
		if( !hasAttribute(attributeName) )
			SchemaParseException.raise(
				this, SchemaParseException.ERR_MISSING_ATTRIBUTE, attributeName );
		
		return getAttribute(attributeName);
	}

	/**
	 * reads attribute value from an element
	 * If the specified attribute is not present, returns specified default value
	 */
	protected String getOptionalAttribute( String attributeName, String defaultValue )
	{
		if( !hasAttribute(attributeName) )		return defaultValue;
		else									return getAttribute(attributeName);
	}

}
