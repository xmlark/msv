package com.sun.tranquilo.datatype;

public class MaxLengthFacet extends DataTypeWithValueConstraintFacet
{
	protected final int maxLength;
	
	protected MaxLengthFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName,baseType,FACET_MAXLENGTH,facets);
	
		maxLength = facets.getNonNegativeInteger(FACET_MAXLENGTH);
		facets.consume(FACET_MAXLENGTH);

		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_MAXLENGTH);
		if(o!=null && ((MaxLengthFacet)o).maxLength < this.maxLength )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_MAXLENGTH, o.getName() );
		
		// consistency with minLength is checked in DataTypeImpl.derive method.
	}
	
	public Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null || ((Discrete)baseType).countLength(literal)<maxLength)	return null;
		return o;
	}
}
