package com.sun.tranquilo.datatype;

/**
 * "negativeInteger" and negativeInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#negativeInteger for the spec
 */
public class NegativeIntegerType extends IntegerType
{
	/** singleton access to the plain negativeInteger type */
	public static NegativeIntegerType theInstance =
		new NegativeIntegerType("negativeInteger",null,null,null,null);
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		final IntegerValueType v = new IntegerValueType(lexicalValue);
		if( !v.isNegative() )	throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new NegativeIntegerType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from negativeInteger by restriction.
	 * 
	 * To derive a datatype by restriction from negativeInteger, call derive method.
	 * This method is only accessible within this class.
	 */
	private NegativeIntegerType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
