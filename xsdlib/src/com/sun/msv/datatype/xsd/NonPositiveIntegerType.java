package com.sun.tranquilo.datatype;

/**
 * "nonPositiveInteger" and nonPositiveInteger-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#nonPositiveInteger for the spec
 */
public class NonPositiveIntegerType extends IntegerType
{
	/** singleton access to the plain nonPositiveInteger type */
	public static NonPositiveIntegerType theInstance =
		new NonPositiveIntegerType("nonPositiveInteger",null,null,null,null);
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{
		// conformance of the lexicalValue with respect to integer's lexical space
		// will be tested in IntegerValueType.
		final IntegerValueType v = new IntegerValueType(lexicalValue);
		if( !v.isNonPositive() )	throw new ConvertionException();
		return v;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new NonPositiveIntegerType( newName,
			RangeFacet.merge(this,this.range,facets),
			PrecisionScaleFacet.merge(this.precisionScale,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	/**
	 * constructor for derived-type from nonPositiveInteger by restriction.
	 * 
	 * To derive a datatype by restriction from nonPositiveInteger, call derive method.
	 * This method is only accessible within this class.
	 */
	private NonPositiveIntegerType( String typeName, 
					    RangeFacet range, PrecisionScaleFacet precisionScale, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName, range, precisionScale, pattern, enumeration );
	}
	
}
