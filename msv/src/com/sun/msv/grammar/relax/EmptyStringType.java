package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.datatype.*;

/**
 * 'emptyString' type of RELAX.
 * 
 * this type accepts nothing but ""
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
