package com.sun.tranquilo.datatype;

/**
 * "positiveInteger" and positiveInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#positiveInteger for the spec
 */
public class PositiveIntegerType extends IntegerType
{
	/** singleton access to the plain positiveInteger type */
	public static PositiveIntegerType theInstance =
		new PositiveIntegerType("positiveInteger",null,null,null,null);
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		final IntegerValueType v = new IntegerValueType(lexicalValue);
		if( !v.isPositive() )       throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new PositiveIntegerType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from positiveInteger by restriction.
	 * 
	 * To derive a datatype by restriction from positiveInteger, call derive method.
	 * This method is only accessible within this class.
	 */
	private PositiveIntegerType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
