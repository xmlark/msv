/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.StringType;
import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.util.DataTypeRef;

/**
 * chunk of string.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class StringToken extends Token
{
	protected final String literal;
	protected final ValidationContextProvider context;
	protected final boolean ignorable;
	/**
	 * if this field is non-null,
	 * this field will receive assigned DataType object.
	 */
	protected final DataTypeRef refType;
	protected boolean saturated = false;
	
	public StringToken( String literal, ValidationContextProvider context )
	{
		this(literal,context,null);
	}
	
	public StringToken( String literal, ValidationContextProvider context, DataTypeRef refType )
	{
		this.literal = literal;
		this.context = context;
		this.refType = refType;
		this.ignorable = literal.trim().length()==0;
	}
	
	/** TypedStringExp can consume this token if its datatype can accept this string */
	boolean match( TypedStringExp exp )
	{
		if(!exp.dt.verify( literal, context ))	return false;
		if(refType!=null)	assignType(exp.dt);
		return true;
	}
	
	// anyString can match any string
	boolean matchAnyString()
	{
		if(refType!=null)	assignType(StringType.theInstance);
		return true;
	}

	private void assignType( DataType dt )
	{
		if(saturated)
			// more than one types are assigned. roll back to null
			refType.type=null;
		else
		{// this is the first assignment. remember this value.
			refType.type=dt;
			saturated=true;
		}
	}
		
	/** checks if this token is ignorable.
	 * 
	 * StringToken is ignorable when it matches [ \t\r\n]*
	 */
	boolean isIgnorable() { return ignorable; }
}
