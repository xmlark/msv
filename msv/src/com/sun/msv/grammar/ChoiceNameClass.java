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
 * Union of two NameClasses.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ChoiceNameClass implements NameClass
{
	public final NameClass nc1;
	public final NameClass nc2;
	
	public boolean accepts( String namespaceURI, String localPart )
	{
		return nc1.accepts(namespaceURI,localPart)
			|| nc2.accepts(namespaceURI,localPart);
	}
	
	public Object visit( NameClassVisitor visitor ) { return visitor.onChoice(this); }
	
	public ChoiceNameClass( NameClass nc1, NameClass nc2 )
	{
		this.nc1 = nc1;
		this.nc2 = nc2;
	}
}
