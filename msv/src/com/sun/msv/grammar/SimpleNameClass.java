/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

/**
 * a NameClass that accepts only one fixed tag name.
 */
public final class SimpleNameClass implements NameClass
{
	public final String	namespaceURI;
	public final String localName;
	
	public boolean accepts( String namespaceURI, String localName )
	{
		return	( this.namespaceURI.equals(namespaceURI) || NAMESPACE_WILDCARD.equals(namespaceURI) )
			&&  ( this.localName.equals(localName) || LOCALNAME_WILDCARD.equals(localName) );
	}
	
	public SimpleNameClass( String namespaceURI, String localName )
	{
		this.namespaceURI	= namespaceURI;
		this.localName		= localName;
	}
	
	public String toString()
	{
		if( namespaceURI.length()==0 )	return localName;
		else							return /*namespaceURI+":"+*/localName;
	}
}
