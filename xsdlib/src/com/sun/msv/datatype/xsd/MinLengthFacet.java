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
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_MINLENGTH);
		if(o!=null && ((MinLengthFacet)o).minLength > this.minLength )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_MINLENGTH, o.getName() );
		
		// consistency with maxLength is checked in DataTypeImpl.derive method.
	}
	
	public Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null || ((Discrete)baseType).countLength(literal)<minLength)	return null;
		return o;
	}
}
