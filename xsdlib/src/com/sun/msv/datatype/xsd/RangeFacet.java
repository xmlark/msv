package com.sun.tranquilo.datatype;

/**
 * "(max|min)(In|Ex)clusive" facet validator
 * 
 * this class also detects inconsistent facet setting
 * (for example, maxInclusive=50 and maxExclusive=45)
 */
public class RangeFacet
{
	private final Pair	maxInclusive;
	private final Pair	maxExclusive;
	private final Pair	minExclusive;
	private final Pair	minInclusive;
	
	private static class Pair
	{
		public final Comparable value;
		public final boolean isFixed;
		public Pair( Comparable value, boolean isFixed )
		{
			this.value	= value;
			this.isFixed= isFixed;
		}
	}
	
	/** base facet that has to be also checked */
	private final RangeFacet		base;
	
	private static Pair convertValue(
		DataType parentType, Pair basePair,
		Facets facets, String facetName )
		throws BadTypeException
	{
		if( facets.contains(facetName) )
		{
			if( basePair!=null && basePair.isFixed )
				throw new BadTypeException(
					BadTypeException.ERR_OVERRIDING_FIXED_FACET, facetName );
			
			final String lexValue = facets.getFacet(facetName);
			try
			{
				Pair p = new Pair(
					(Comparable)parentType.convertValue( lexValue ),
					facets.isFixed(facetName) );
				facets.consume(facetName);
				return p;
			}
			catch( ConvertionException e )
			{
				throw new BadTypeException(
					BadTypeException.ERR_INAPPROPRIATE_VALUE_FOR_X,
					lexValue, facetName );
			}
		}
		else
			return null;
	}
	
	private RangeFacet( DataType parentType, RangeFacet base, Facets facets )
		throws BadTypeException
	{
		this.base = base;
		
		// retrieve values, and performs necessary checks
		maxInclusive = convertValue( parentType, base.maxInclusive, facets, "maxInclusive" );
		minInclusive = convertValue( parentType, base.minInclusive, facets, "minInclusive" );
		maxExclusive = convertValue( parentType, base.maxExclusive, facets, "maxExclusive" );
		minExclusive = convertValue( parentType, base.minExclusive, facets, "minExclusive" );
		
		// consistency check
		if( maxInclusive!=null && maxExclusive!=null )
			throw new BadTypeException(
				BadTypeException.ERR_EXCLUSIVE_FACETS_SPECIFIED,
				"maxInclusive", "maxExclusive" );
		if( minInclusive!=null && minExclusive!=null )
			throw new BadTypeException(
				BadTypeException.ERR_EXCLUSIVE_FACETS_SPECIFIED,
				"minInclusive", "minExclusive" );
		
		Pair upperBound = (maxInclusive!=null) ? maxInclusive : maxExclusive;
		Pair lowerBound = (minInclusive!=null) ? minInclusive : minExclusive;
		
		if( upperBound!=null && lowerBound!=null )
		{
			if( upperBound.value.compareTo( lowerBound.value ) < 0 )
				throw new BadTypeException(
					BadTypeException.ERR_ILLEGAL_MAX_MIN_ORDER,
					(maxInclusive!=null)?"maxInclusive":"maxExclusive",
					(minInclusive!=null)?"minInclusive":"minExclusive" );
			
			// TODO : should it be considered as an error to specify
			//        maxExclusive="0" and minExclusive="0", which
			//		  do not accept anything.
			//
			//		  But the problem is, if this is an error,
			//		  minExclusive="0" and maxExclusive="0 + float epsilon"
			//		  should be also an error, since it doesn't accept anything.
			//
			//		  Another problem is, due to the implementation of
			//		  Float.compareTo, "-0" < "+0".
		}
	}
	
	public boolean verify( Comparable valueObject )
	{
		// base object must be satisfied.
		if( base!=null && !base.verify(valueObject) )	return false;
		
		if( maxInclusive!=null && maxInclusive.value.compareTo(valueObject) >0 ) return false;
		if( maxExclusive!=null && maxExclusive.value.compareTo(valueObject)>=0 ) return false;
		if( minInclusive!=null && minInclusive.value.compareTo(valueObject) <0 ) return false;
		if( minExclusive!=null && minExclusive.value.compareTo(valueObject)<=0 ) return false;
		return true;
	}
	
	static RangeFacet create( DataType parentType, Facets facets )
		throws BadTypeException
	{
		return merge( parentType, null, facets );
	}
	
	static RangeFacet merge( DataType parentType, RangeFacet base, Facets facets )
		throws BadTypeException
	{
		RangeFacet r = new RangeFacet(parentType,base,facets);
		if( r.maxExclusive==null && r.maxInclusive==null
		&&  r.minExclusive==null && r.minInclusive==null )
			return null;		// no range facet is specified
		
		return r;
	}
}
