/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.util;

import org.xml.sax.Attributes;
import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * immutable start tag information
 */
public class StartTagInfo
{
	public final String		namespaceURI;
	public final String		localName;
	public final String		qName;
	public final Attributes	attributes;
	/** object that provides additional information which is necessary
	 * for validating some datatypes
	 */
	public final ValidationContextProvider context;
	
	public StartTagInfo(
		String namespaceURI, String localName, String qName,
		Attributes attributes, ValidationContextProvider context )
	{
		this.namespaceURI	= namespaceURI;
		this.localName		= localName;
		this.qName			= qName;
		this.attributes		= attributes;
		this.context		= context;
	}
	
	public final boolean containsAttribute( String attrName )
	{ return containsAttribute("",attrName); }
	public final boolean containsAttribute( String namespaceURI, String attrName )
	{
		return attributes.getIndex(namespaceURI,attrName)!=-1;
	}
	
	/**
	 * gets value of the specified attribute.
	 * 
	 * @return null		attribute does not exist.
	 */
	public final String getAttribute( String attrName )
	{ return getAttribute("",attrName); }

	public final String getAttribute( String namespaceURI, String attrName )
	{
		return attributes.getValue(namespaceURI,attrName);
	}
}
