package com.sun.tranquilo.datatype;

import java.util.Map;
import java.util.Vector;
import java.util.Iterator;

/**
 * Type-safe map that contains facets bundle.
 *
 */
public class Facets
{
	/** storage for non-repeatable facets */
	private final Map impl = new java.util.HashMap();
	
	private static boolean isRepeatable( String facetName )
	{
		return facetName.equals("enumeration") || facetName.equals("pattern");
	}
	
	public Facets() {}
	
	/** shallow copy constructor.
	 * 
	 * this constructor does NOT produce a deep copy.
	 * "consume" method and read operations can be safely used,
	 * but do not call other methods.
	 */
	public Facets( Facets rhs )
	{
		this.impl.putAll(rhs.impl);
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
		return (String)((FacetInfo)impl.get(facetName)).value;
	}
	
	/**
	 * gets a value of repeatable facet
	 * 
	 * the behavior is undefined when the specified facetName doesn't exist
	 * in this map.
	 */
	public Vector getVector(String facetName)
	{
		return (Vector)((FacetInfo)impl.get(facetName)).value;
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
	
	/** merge the contents of rhs into this object.
	 *
	 * "fixed" properties are ignored.
	 */
	public void merge( Facets rhs )
		throws BadTypeException
	{
		Iterator itr = rhs.impl.keySet().iterator();
		while(itr.hasNext())
		{
			String key = (String)itr.next();
			if( isRepeatable(key) )
			{
				Vector vec = rhs.getVector(key);
				for( int i=0; i<vec.size(); i++ )
					add( key,(String)vec.elementAt(i),false );
			}
			else
				add( key, rhs.getFacet(key), false );
		}
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
	
	/** dumps the contents to the given object.
	 * 
	 * this method is for debug use only.
	 */
	public void dump( java.io.PrintStream out )
	{
		Iterator itr = impl.keySet().iterator();
		while(itr.hasNext())
		{
			String facetName = (String)itr.next();
			FacetInfo fi = (FacetInfo)impl.get(facetName);
			
			if( fi.value instanceof String )
				out.println( facetName + " : " + (String)fi.value );
			else
			{
				out.println( facetName + " :");
				Vector v = (Vector)fi.value;
				for( int i=0; i<v.size(); i++ )
					out.println( "  " +v.elementAt(i) );
			}
		}
	}
	
	/** gets names of the facets in this object
	 *
	 * this method is used to produce error messages
	 */
	public String getFacetNames()
	{
		String r="";
		Iterator itr = impl.keySet().iterator();
		while(itr.hasNext())
		{
			if(r.length()!=0)	r+=", ";
			r += (String)itr.next();
		}
		
		return r;
	}
}