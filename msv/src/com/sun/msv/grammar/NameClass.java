/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * validator of (namespaceURI,localPart) pair.
 * 
 * This is equivalent to TREX's "name class".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface NameClass
{
	/**
	 * checks if this name class accepts given namespace:localName pair.
	 * 
	 * @param namespaceURI
	 *		namespace URI to be tested. If this value equals to
	 *		NAMESPACE_WILDCARD, implementation must assume that
	 *		valid namespace is specified. this twist will be used to
	 *		recovery from error.
	 * 
	 * @param localName
	 *		local part to be tested. As with namespaceURI, LOCALNAME_WILDCARD
	 *		will acts as a wild card.
	 * 
	 * @return
	 *		true if the pair is accepted,
	 *		false otherwise.
	 */
	boolean accepts( String namespaceURI, String localName );
	
	/**
	 * visitor pattern support
	 */
	Object visit( NameClassVisitor visitor );
	
	public static final String NAMESPACE_WILDCARD = "*";
	
	public static final String LOCALNAME_WILDCARD = "*";
}
