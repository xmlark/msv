package com.sun.tranquilo.datatype;

public class MaxInclusiveFacet extends RangeFacet
{
	protected MaxInclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MAXINCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.GREATER || r==Comparator.EQUAL;
	}
}
