package com.sun.tranquilo.datatype;

import java.util.Hashtable;

/**
 * "length", "minLength", and "maxLength" facet validator
 * 
 * this class also detects inconsistent facet setting
 * (for example, minLength=100 and maxLength=0)
 */
public class LengthFacet extends DataTypeWithValueConstraintFacet
{
	private final int length;
	
	protected LengthFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName,baseType,FACET_LENGTH,facets);
	
		length = facets.getNonNegativeInteger(FACET_LENGTH);
		facets.consume(FACET_LENGTH);
	}
	
	public Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null || ((Discrete)baseType).countLength(literal)!=length)	return null;
		return o;
	}
}
