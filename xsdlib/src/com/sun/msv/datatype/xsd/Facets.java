package com.sun.tranquilo.datatype;

import java.util.Map;
import java.util.Vector;

/**
 * Type-safe map that contains facets bundle.
 *
 */
class Facets
{
	/** storage for non-repeatable facets */
	private final Map impl = new java.util.HashMap();
	
	private static boolean isRepeatable( String facetName )
	{
		return facetName.equals("enumeration") || facetName.equals("pattern");
	}
	
	/** adds a facet to this set.
	 *
	 * @exception	BadTypeException
	 *		when given facet is already specified
	 */
	public void add( String name, String value, boolean fixed )
		throws BadTypeException
	{
		if( isRepeatable(name) )
		{
			FacetInfo fi;
			if( impl.containsKey(name) )
				fi = (FacetInfo)impl.get(name);
			else
				impl.put(name, fi=new FacetInfo(new Vector(),fixed));
			
			((Vector)fi.value).add(value);
			// TODO : what shall we do if
			// <enumeration value="a" fixed="true" />
			// <enumeration value="b" fixed="false" />
			fi.fixed |= fixed;
		}
		else
		{
			if( impl.containsKey(name) )
				throw new BadTypeException(
					BadTypeException.ERR_DUPLICATE_FACET, name );
			impl.put(name, new FacetInfo(value,fixed));
		}
	}
	
	/**
	 * returns true if that facet is fixed.
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public boolean isFixed( String facetName )
	{
		return ((FacetInfo)impl.get(facetName)).fixed;
	}
	
	/**
	 * gets a value of non-repeatable facet
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public String getFacet( String facetName )
	{
		return (String)impl.get(facetName);
	}
	
	/**
	 * gets a value of repeatable facet
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public Vector getVector(String facetName)
	{
		return (Vector)impl.get(facetName);
	}
	
	/**
	 * gets a value of non-repeatable facet as a positive integer
	 *
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 * 
	 * @exception BadTypeException
	 *		if the parameter cannot be parsed as a positive integer
	 */
	public int getPositiveInteger( String facetName )
		throws BadTypeException
	{
		try
		{
			// TODO : is this implementation correct?
			int value = Integer.parseInt(getFacet(facetName));
			if( value>0 )	return value;
		}
		catch( NumberFormatException e )
		{ ; }
		
		throw new BadTypeException(
			BadTypeException.ERR_FACET_MUST_BE_POSITIVE_INTEGER,
			facetName );
	}
	
	/**
	 * gets a value of non-repeatable facet as a non-negative integer
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 * 
	 * @exception BadTypeException
	 *		if the parameter cannot be parsed as a non-negative integer
	 */
	public int getNonNegativeInteger( String facetName )
		throws BadTypeException
	{
		try
		{
			// TODO : is this implementation correct?
			int value = Integer.parseInt(getFacet(facetName));
			if( value>=0 )		return value;
		}
		catch( NumberFormatException e )
		{ ; }
		
		throw new BadTypeException(
			BadTypeException.ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER,
			facetName );
	}
	
	/** checks if the specified facet was added to this map  */
	public boolean contains( String facetName )
	{
		return impl.containsKey(facetName);
	}
	
	/** marks the specified facet as "consumed".
	 * 
	 * consumed facet will be removed from the map.
	 *
	 * if any facet is left unconsumed, it should be reported as an error.
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public void consume( String facetName )
	{
		impl.remove(facetName);
	}
	
	/** returns true if no facet is added */
	public boolean isEmpty()
	{
		return impl.isEmpty();
	}
	
	private static class FacetInfo
	{
		public Object value;
		public boolean fixed;
		public FacetInfo( Object value, boolean fixed )
		{
			this.value = value;
			this.fixed = fixed;
		}
	}
}