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
 * a NameClass that matches any name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class AnyNameClass implements NameClass
{
	public boolean accepts( String namespaceURI, String localName )
	{
		return true;
	}
	
	/** singleton instance */
	public static final NameClass theInstance = new AnyNameClass();
	
	private AnyNameClass() {}
	
	public String toString()	{ return "*:*"; }
}
