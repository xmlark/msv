package com.sun.tranquilo.datatype;

/**
 * "unsignedInt" and unsignedInt-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedInt for the spec
 *
 * We don't have language support for unsigned datatypes, so things are not so easy.
 * UnsignedIntType uses a LongType as a base implementation, for the convenience and
 * faster performance.
 */
public class UnsignedIntType extends LongType
{
	/** singleton access to the plain unsignedInt type */
	public static UnsignedIntType theInstance
		= new UnsignedIntType("unsignedInt",null,null,null,null);
	
    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final long upperBound
                            = 4294967295L;
    
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
        Long v = (Long)super.convertValue(lexicalValue);
        if( v.longValue()<0 )               throw new ConvertionException();
        if( v.longValue()>upperBound )      throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new UnsignedIntType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from unsignedInt by restriction.
	 * 
	 * To derive a datatype by restriction from unsignedInt, call derive method.
	 * This method is only accessible within this class.
	 */
	private UnsignedIntType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
