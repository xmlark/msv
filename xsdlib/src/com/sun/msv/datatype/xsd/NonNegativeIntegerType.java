package com.sun.tranquilo.datatype;

/**
 * "nonNegativeInteger" and nonNegativeInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#nonNegativeInteger for the spec
 */
public class NonNegativeIntegerType extends IntegerType
{
	/** singleton access to the plain nonNegativeInteger type */
	public static NonNegativeIntegerType theInstance =
		new NonNegativeIntegerType("nonNegativeInteger",null,null,null,null);
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		final IntegerValueType v = new IntegerValueType(lexicalValue);
		if( !v.isNonNegative() )	throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new NonNegativeIntegerType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from nonNegativeInteger by restriction.
	 * 
	 * To derive a datatype by restriction from nonNegativeInteger, call derive method.
	 * This method is only accessible within this class.
	 */
	private NonNegativeIntegerType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
