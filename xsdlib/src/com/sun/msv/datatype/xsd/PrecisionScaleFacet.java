package com.sun.tranquilo.datatype;

/**
 * 'precision' and 'scale' facet.
 *
 * this class holds these facet information and performs validation.
 */
public class PrecisionScaleFacet
{
	/** maximum number of total digits. -1 if unspecified. */
	private final int		precision;
	/** maximum number of fractional digits. -1 if unspecified. */
	private final int		scale;
	
	/** a flag that indicates precision facet is fixed */
	private final boolean	fixedPrecision;
	/** a flag that indicates scale facet is fixed */
	private final boolean	fixedScale;
	
	private PrecisionScaleFacet(
		int precision, boolean fixedPrecision, int scale, boolean fixedScale )
	{
		this.precision		= precision;
		this.fixedPrecision	= fixedPrecision;
		this.scale			= scale;
		this.fixedScale		= fixedScale;
	}
	
	public boolean verify( Object valueObject, PrecisionScaleInterpreter interpreter )
	{
		// TODO : there must be a better implementation
		if( precision!=-1 && interpreter.getPrecisionForValueObject(valueObject)>precision)
			return false;
		if( scale!=-1 && interpreter.getScaleForValueObject(valueObject)>scale )
			return false;
		
		return true;
	}
	
	public static PrecisionScaleFacet create( Facets facets )
		throws BadTypeException
	{
		return merge( null, facets );
	}
	
	public static PrecisionScaleFacet merge( PrecisionScaleFacet base, Facets facets )
		throws BadTypeException
	{
		int scale=-1,precision=-1; boolean fixedPrecision=false,fixedScale=false;
		
		// retrieves value
		if( facets.contains("precision") )
		{
			precision = facets.getPositiveInteger("precision");
			fixedPrecision = facets.isFixed("precision");
			facets.consume("precision");
			
			if( base!=null && base.fixedPrecision )
				throw new BadTypeException(
					BadTypeException.ERR_OVERRIDING_FIXED_FACET, "precision" );
		}
		
		if( facets.contains("scale") )
		{
			scale = facets.getNonNegativeInteger("scale");
			fixedScale = facets.isFixed("scale");
			facets.consume("scale");
			
			if( base!=null && base.fixedScale )
				throw new BadTypeException(
					BadTypeException.ERR_OVERRIDING_FIXED_FACET, "scale" );
		}
		
		// if nothing is specified, just use base specification
		if( precision==-1 && scale==-1 )	return base;
		
		// merge with specification in base
		fixedScale		|= base.fixedScale;
		fixedPrecision	|= base.fixedPrecision;

		// scale facet : smaller number is stricter
		if( scale==-1 )					scale = base.scale;
		else if( base.scale!=-1 )		scale = Math.min(scale,base.scale);
		
		// precision facet : smaller number is stricter
		if( precision==-1 )				precision = base.precision;
		else if( base.precision!=-1 )	precision = Math.min(precision,base.precision);
		
		// consistency check
		if( precision!=-1 && scale!=-1 && scale>precision )
			throw new BadTypeException( BadTypeException.ERR_SCALE_IS_GREATER_THAN_PRECISION );
		
		return new PrecisionScaleFacet(
			precision, fixedPrecision, scale, fixedScale );
	}
}
