package com.sun.tranquilo.datatype;

public class MinLengthFacet extends DataTypeWithValueConstraintFacet
{
	private final int minLength;
	
	protected MinLengthFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName,baseType,FACET_MINLENGTH,facets);
	
		minLength = facets.getNonNegativeInteger(FACET_MINLENGTH);
		facets.consume(FACET_MINLENGTH);
	}
	
	public Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null || ((Discrete)baseType).countLength(literal)<minLength)	return null;
		return o;
	}
}
