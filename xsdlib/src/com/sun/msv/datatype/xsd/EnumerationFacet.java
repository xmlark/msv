package com.sun.tranquilo.datatype;

import java.util.Set;
import java.util.Vector;

/**
 * "enumeration" facets validator
 */
public class EnumerationFacet
{
	/** set of valid values */
	private final Set values;
	
	/** a flag that indicates that this enumeration is fixed and
	 * therefore no more derivation is possible
	 */
	private final boolean isFixed;
	
	
	/** enumeration facets of the base type. can be null */
	private final EnumerationFacet base;
													 
	public boolean verify( Object valueOfValueSpace )
	{
		// value must be accepted as a base type.
		if( base!=null && !base.verify(valueOfValueSpace) )
			return false;
		return values.contains(valueOfValueSpace);
	}
	
	private EnumerationFacet( EnumerationFacet base, Set valueObjects, boolean fixed )
	{
		this.base	= base;
		this.values = valueObjects;
		this.isFixed= fixed;
	}
	
	/**
	 * returns a EnumerationFacet object if "enumeration" facet is specified.
	 * Otherwise returns null.
	 */
	public static EnumerationFacet create( DataType parentType, Facets facets )
		throws BadTypeException
	{
		return merge( parentType, null, facets );
	}
	
	public static EnumerationFacet merge( DataType parentType, EnumerationFacet base, Facets facets )
		throws BadTypeException
	{
		// no enumeration facet is specified.
		if( !facets.contains("enumeration") )	return base;

		// makes sure that base EnumerationFacet is not fixed
		if( base!=null && base.isFixed )
			throw new BadTypeException(
				BadTypeException.ERR_OVERRIDING_FIXED_FACET, "enumeration" );

		// converts all lexical values into the value of value space.
		Set valueObjs = new java.util.HashSet();
		Vector lexValues = (Vector)facets.getVector("enumeration");
		int len = lexValues.size();
		
		for( int i=0; i<len; i++ )
			try
			{
				valueObjs.add( parentType.convertValue(
					(String)lexValues.elementAt(i) ) );
			}
			catch( ConvertionException e )
			{
				throw new BadTypeException(
					BadTypeException.ERR_INVALID_VALUE_FOR_THIS_TYPE,
					lexValues.elementAt(i),
					parentType.getName() );
			}
		
				
		EnumerationFacet r =
			new EnumerationFacet(base,valueObjs,facets.isFixed("enumeration"));
		facets.consume("enumeration");
		
		return r;
	}
}
