package com.sun.tranquilo.datatype;

abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet
{
	DataTypeWithValueConstraintFacet(
		String typeName, DataTypeImpl baseType, String facetName, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, facetName, facets );
	}
	
	final protected boolean needValueCheck() { return true; }
	
	protected final boolean checkFormat( String literal, ValidationContextProvider context )
	{
		// since we always return true for needValueCheck,
		// this method should never be called.
		throw new IllegalStateException();
	}
}
