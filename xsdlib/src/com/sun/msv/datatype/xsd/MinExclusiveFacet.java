package com.sun.tranquilo.datatype;

public class MinExclusiveFacet extends RangeFacet
{
	protected MinExclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MINEXCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.LESS;
	}
}
