package com.sun.tranquilo.datatype;

abstract class DataTypeWithLexicalConstraintFacet extends DataTypeWithFacet
{
	DataTypeWithLexicalConstraintFacet(
		String typeName, DataTypeImpl baseType, String facetName, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, facetName, facets );
	}
	
	// this class does not perform any lexical check.
	protected final boolean checkFormat( String literal )
	{
		if(!baseType.checkFormat(literal))	return false;
		return checkLexicalConstraint(literal);
	}
	
	public final Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o!=null && !checkLexicalConstraint(literal) )	return null;
		return o;
	}

	protected abstract boolean checkLexicalConstraint( String literal );
}
