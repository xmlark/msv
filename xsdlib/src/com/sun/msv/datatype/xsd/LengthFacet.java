package com.sun.tranquilo.datatype;

import java.util.Hashtable;

/**
 * "length", "minLength", and "maxLength" facet validator
 * 
 * this class also detects inconsistent facet setting
 * (for example, minLength=100 and maxLength=0)
 */
public class LengthFacet
{
	/** value of specified "minLength". If not specified, this value is set to -1 */
	final private int		minLength;
	/** value of specified "maxLength". If not specified, this value is set to -1 */
	final private int		maxLength;
	/** value of specified "length". If not specified, this value is set to -1 */
	final private int		length;
	
	/** a flag that indicates minLength facet is fixed */
	final private boolean	fixedMinLength;
	/** a flag that indicates maxLength facet is fixed */
	final private boolean	fixedMaxLength;
	/** a flag that indicates length facet is fixed */
	final private boolean	fixedLength;
	
	/** special LengthFacet that accepts nothing */
	final static private LengthFacet theNone = new LengthFacet( -2,false,-1,false,-1,false );
	
	/**
	 * verifies that the value is within the range specified by length-related facets.
	 */
	public boolean verify( int dataLength )
	{
		// if "length" facet is specified, it must be the sole facet
		if( length!=-1 )	return length==dataLength;
		
		// otherwise, check the other two.
		if( minLength!=-1 && dataLength<minLength )		return false;
		if( maxLength!=-1 && dataLength>maxLength )		return false;
		return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( int dataLength )
	{
		// TODO: implement this
		throw new UnsupportedOperationException();
	}

	private LengthFacet(
		int    length, boolean fixedLength,
		int minLength, boolean fixedMinLength,
		int maxLength, boolean fixedMaxLength )
	{
		this.length			= length;
		this.fixedLength	= fixedLength;
		this.minLength		= minLength;
		this.fixedMinLength	= fixedMinLength;
		this.maxLength		= maxLength;
		this.fixedMaxLength	= fixedMaxLength;
	}
	
	/**
	 * returns a LengthFacet object if length-related facets are specified.
	 * Otherwise returns null.
	 */
	public static LengthFacet create( Facets facets )
		throws BadTypeException
	{
		return merge( null, facets );
	}
	
	/**
	 * creates a new LengthFacet whose constraints are
	 * intersection of one LengthFacet object and facets
	 * 
	 * if lhs is null and no further constraints are given,
	 * then returns null.
	 *
	 * if a facet that is fixed in lhs is specified in rhs,
	 * BadTypeException is thrown.
	 */
	public static LengthFacet merge( LengthFacet base, Facets facets )
		throws BadTypeException
	{
		int length=-1,minLength=-1,maxLength=-1;
		boolean fixedLength=false,fixedMinLength=false,fixedMaxLength=false;
		
		// if no facets is specified, just return base
		if( !facets.contains("length")
		&&  !facets.contains("minLength")
		&&  !facets.contains("maxLength") )
			return base;
		
		// once if datatype accepts nothing, it never accepts anything by derivation
		if( base==theNone )
			return base;
		
		if( base!=null )
		{
			fixedLength		= base.fixedLength;
			fixedMaxLength	= base.fixedMaxLength;
			fixedMinLength	= base.fixedMinLength;
		}
		
		if( facets.contains("length") )
		{
			length = facets.getNonNegativeInteger("length");
			fixedLength = facets.isFixed("length");
			

			// makes sure that specifying length makes sense
			// with respect to facets specified in 'base'
			if( base!=null )
			{
				if( base.fixedLength )
					throw new BadTypeException(
						BadTypeException.ERR_OVERRIDING_FIXED_FACET, "length" );
				
				// TODO : should we throw an exception here?
				if( base.length!=-1 && base.length!=length )
					return theNone;
				if( base.minLength!=-1 && length<base.minLength )
					return theNone;
				if( base.maxLength!=-1 && base.maxLength<length )
					return theNone;
			}

			// makes sure that two other facets are not specified.
			if( facets.contains("minLength") )
				throw new BadTypeException(
					BadTypeException.ERR_LENGTH_AND_X_IS_EXCLUSIVE,
					"minLength" );
			if( facets.contains("maxLength") )
				throw new BadTypeException(
					BadTypeException.ERR_LENGTH_AND_X_IS_EXCLUSIVE,
					"maxLength" );
			
			facets.consume("length");
		}
		else
		{
			if( facets.contains("minLength") )
			{
				minLength = facets.getNonNegativeInteger("minLength");
				fixedMinLength = facets.isFixed("minLength");
				facets.consume("minLength");
				
				if( base!=null && base.fixedMinLength )
					throw new BadTypeException(
						BadTypeException.ERR_OVERRIDING_FIXED_FACET, "minLength" );
				
				// merge a constraint from base type
				if( minLength < base.minLength )	minLength = base.minLength;
			}
			
			if( facets.contains("maxLength") )
			{
				maxLength = facets.getNonNegativeInteger("maxLength");
				fixedMaxLength = facets.isFixed("maxLength");
				facets.consume("maxLength");
				
				if( base!=null && base.fixedMaxLength )
					throw new BadTypeException(
						BadTypeException.ERR_OVERRIDING_FIXED_FACET, "maxLength" );
				
				// merge a constraint from base type
				if( minLength < base.minLength )	minLength = base.minLength;
			}
			
			if( minLength!=-1 && maxLength!=-1 && minLength>maxLength )
				// minLength > maxLength is inconsistent.
				// note that minLength==maxLength is valid.
				throw new BadTypeException(
					BadTypeException.ERR_INCONSISTENT_MINMAX_LENGTH );
		}
		
		return new LengthFacet(
			length,fixedLength,
			minLength, fixedMinLength,
			maxLength, fixedMaxLength );
	}
}
