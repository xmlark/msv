package com.sun.tranquilo.datatype;

public class MinInclusiveFacet extends RangeFacet
{
	protected MinInclusiveFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_MININCLUSIVE, facets );
		
		// TODO : consistency check
	}
	
	protected final boolean rangeCheck( int r )
	{
		return r==Comparator.LESS || r==Comparator.EQUAL;
	}
}
