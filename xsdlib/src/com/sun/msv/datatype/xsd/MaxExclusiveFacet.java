package com.sun.tranquilo.datatype;

public class MaxExclusiveFacet extends RangeFacet
{
	protected MaxExclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MAXEXCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.GREATER;
	}
}
