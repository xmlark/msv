package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * chunk of string
 */
public class StringToken extends Token
{
	protected final String literal;
	protected final ValidationContextProvider context;
	protected final boolean ignorable;							  
	
	public StringToken( String literal, ValidationContextProvider context )
	{
		this.literal = literal;
		this.context = context;
		this.ignorable = literal.trim().length()==0;
	}
	
	/** TypedStringExp can consume this token if its datatype can accept this string */
	boolean match( TypedStringExp exp )		{ return exp.dt.verify( literal, context ); }
	
	// anyString can match any string
	boolean matchAnyString()				{ return true; }
	
	/** checks if this token is ignorable.
	 * 
	 * StringToken is ignorable when it matches [ \t\r\n]*
	 */
	boolean isIgnorable() { return ignorable; }
}
