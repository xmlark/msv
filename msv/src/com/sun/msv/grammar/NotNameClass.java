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
 * NameClass that acts a not operator.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class NotNameClass implements NameClass
{
	public final NameClass child;

	public boolean accepts( String namespaceURI, String localName )
	{
		return !child.accepts(namespaceURI,localName);
	}
	
	public Object visit( NameClassVisitor visitor ) { return visitor.onNot(this); }

	public NotNameClass( NameClass child )
	{
		this.child = child;
	}
	
	public String toString()	{ return "~"+child.toString(); }
}
