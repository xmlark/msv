/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype.conformance;

import org.relaxng.datatype.ValidationContext;

/**
 * dummy implementation of ValidationContextProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class DummyContextProvider implements ValidationContext
{
	private DummyContextProvider() {}
	
	public static final DummyContextProvider theInstance
		= new DummyContextProvider();
	
	public String resolveNamespacePrefix( String prefix )
	{
		if( prefix.equals("foo") )
			return "http://foo.examples.com";
		if( prefix.equals("bar") || prefix.equals("baz") )
			return "http://bar.examples.com";
		if( prefix.equals("") || prefix.equals("emp") )
			return "http://empty.examples.com";
		
		return null;	// undefined
	}
	
	public boolean isUnparsedEntity( String name )
	{
		return name.equals("foo") || name.equals("bar");
	}
}
