package com.sun.tranquilo.datatype;

/**
 * "unsignedByte" and unsignedByte-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedByte for the spec
 *
 * We don't have language support for unsigned datatypes, so things are not so easy.
 * UnsignedByteType uses a IntType as a base implementation, for the convenience and
 * faster performance.
 */
public class UnsignedByteType extends LongType
{
	/** singleton access to the plain unsignedByte type */
	public static UnsignedByteType theInstance
		= new UnsignedByteType("unsignedByte",null,null,null,null);
	
    /** upper bound value. this is the maximum possible valid value as an unsigned int */
    private static final short upperBound
                            = 255;
    
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
        Short v = (Short)super.convertValue(lexicalValue);
        if( v.shortValue()<0 )          throw new ConvertionException();
        if( v.shortValue()>upperBound ) throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new UnsignedByteType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from unsignedByte by restriction.
	 * 
	 * To derive a datatype by restriction from unsignedByte, call derive method.
	 * This method is only accessible within this class.
	 */
	private UnsignedByteType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
