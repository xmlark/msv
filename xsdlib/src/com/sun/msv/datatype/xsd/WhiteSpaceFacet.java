package com.sun.tranquilo.datatype;

public class WhiteSpaceFacet extends DataTypeWithFacet
{
	WhiteSpaceFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName, baseType, FACET_WHITESPACE, facets,
			WhiteSpaceProcessor.get(facets.getFacet(FACET_WHITESPACE)) );
		
		// TODO : consistency check
		
		facets.consume(FACET_WHITESPACE);
	}
	
	protected boolean checkFormat( String content )
	{ return baseType.checkFormat(content); }
	public Object convertToValue( String content )
	{ return baseType.convertToValue(content); }
}
