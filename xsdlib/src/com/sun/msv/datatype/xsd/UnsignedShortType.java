package com.sun.tranquilo.datatype;

/**
 * "unsignedShort" and unsignedShort-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedShort for the spec
 *
 * We don't have language support for unsigned datatypes, so things are not so easy.
 * UnsignedShortType uses a IntType as a base implementation, for the convenience and
 * faster performance.
 */
public class UnsignedShortType extends LongType
{
	/** singleton access to the plain unsignedShort type */
	public static UnsignedShortType theInstance
		= new UnsignedShortType("unsignedShort",null,null,null,null);
	
    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final int upperBound
                            = 65535;
    
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
        Integer v = (Integer)super.convertValue(lexicalValue);
        if( v.intValue()<0 )            throw new ConvertionException();
        if( v.intValue()>upperBound )   throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new UnsignedShortType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from unsignedShort by restriction.
	 * 
	 * To derive a datatype by restriction from unsignedShort, call derive method.
	 * This method is only accessible within this class.
	 */
	private UnsignedShortType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
