package com.sun.tranquilo.datatype;

public class MaxLengthFacet extends DataTypeWithValueConstraintFacet
{
	private final int maxLength;
	
	protected MaxLengthFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName,baseType,FACET_MAXLENGTH,facets);
	
		maxLength = facets.getNonNegativeInteger(FACET_MAXLENGTH);
		facets.consume(FACET_MAXLENGTH);
	}
	
	public Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null || ((Discrete)baseType).countLength(literal)<maxLength)	return null;
		return o;
	}
}
