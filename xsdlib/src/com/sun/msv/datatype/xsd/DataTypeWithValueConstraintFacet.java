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
	
	// this class does not perform any lexical check.
	protected final boolean checkFormat( String literal )
	{
		return baseType.checkFormat(literal);
	}
}
