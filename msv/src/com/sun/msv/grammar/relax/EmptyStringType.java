/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.relax;

import com.sun.msv.datatype.*;

/**
 * 'emptyString' type of RELAX.
 * 
 * this type accepts nothing but "".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EmptyStringType extends ConcreteType
{
	public static final EmptyStringType theInstance = new EmptyStringType();
	private EmptyStringType() { super("emptyString"); }
	
	public int isFacetApplicable( String facetName )
	{
		return NOT_ALLOWED;
	}
	
	public boolean checkFormat( String literal, ValidationContextProvider context )
	{
		return literal.equals("");
	}

	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{
		if( lexicalValue.equals("") )	return lexicalValue;
		else							return null;
	}
}
