package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.ElementExp;

public class ElementToken extends Token
{
	final ElementExp[] acceptedPatterns;
	
	public ElementToken( ElementExp[] acceptedPatterns )
	{
		this.acceptedPatterns = acceptedPatterns;
	}
	
	boolean match( ElementExp exp )
	{
		// since every subpatterns are reused, object identity is enough
		// to judge the equality of patterns
		for( int i=0; i<acceptedPatterns.length; i++ )
			if( acceptedPatterns[i]==exp )	return true;
		return false;
	}
}
