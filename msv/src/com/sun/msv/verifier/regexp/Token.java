package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.*;

/**
 * primitive unit of XML instance.
 * 
 * this object is fed to expression
 */
abstract class Token
{
	/** returns true if the given ElementExp can consume this token  */
	boolean match( ElementExp p )		{ return false;	}
	boolean match( AttributeExp p )		{ return false; }
	/** returns true if the given TypedStringExp can consume this token */
	boolean match( TypedStringExp p )	{ return false; }
	
	/** returns true if anyString pattern can consume this token */
	boolean matchAnyString()				{ return false; }

	/** checks if this token is ignorable. */
	boolean isIgnorable() { return false; }
}