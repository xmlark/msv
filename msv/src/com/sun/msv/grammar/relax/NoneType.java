package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.datatype.*;

/**
 * 'none' type of RELAX.
 * 
 * this type accepts nothing.
 */
public class NoneType extends ConcreteType
{
	public static final NoneType theInstance = new NoneType();
	private NoneType() { super("none"); }
	
	public int isFacetApplicable( String facetName )
	{
		return NOT_ALLOWED;
	}
	
	public boolean checkFormat( String literal, ValidationContextProvider context )
	{ return false; }

	public Object convertToValue( String lexicalValue, ValidationContextProvider context )
	{ return null; }
	
	// TODO: implement diagnoseValue
}