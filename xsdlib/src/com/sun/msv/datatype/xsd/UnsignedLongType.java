package com.sun.tranquilo.datatype;

/**
 * "unsignedLong" and unsignedLong-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#unsignedLong for the spec
 */
public class UnsignedLongType extends IntegerType
{
	/** singleton access to the plain unsignedLong type */
	public static UnsignedLongType theInstance
		= new UnsignedLongType("unsignedLong",null,null,null,null);
	
    /** upper bound value. this is the maximum possible valid value as an unsigned long */
    private static final IntegerValueType upperBound;
	
	static
	{
		try
		{
			upperBound = new IntegerValueType("18446744073709551615");
		}
		catch( ConvertionException e ) { throw new IllegalStateException(); } // not possible
	}
    
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		final IntegerValueType v = new IntegerValueType(lexicalValue);
		if( !v.isNonNegative() )            throw new ConvertionException();
        if( upperBound.compareTo(v)<0 )     throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new UnsignedLongType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from unsignedLong by restriction.
	 * 
	 * To derive a datatype by restriction from unsignedLong, call derive method.
	 * This method is only accessible within this class.
	 */
	private UnsignedLongType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
