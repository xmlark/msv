package com.sun.tranquilo.datatype;

public class MinLengthFacet extends DataTypeWithValueConstraintFacet
{
	protected final int minLength;
	
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
	
	public Object convertToValue( String literal, ValidationContextProvider context )
	{
		Object o = baseType.convertToValue(literal,context);
		if(o==null || ((Discrete)concreteType).countLength(literal)<minLength)	return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		Object o = concreteType.convertToValue(content,context);
		// base type must have accepted this lexical value, otherwise 
		// this method is never called.
		if(o==null)	throw new IllegalStateException();	// assertion
		
		int cnt = ((Discrete)concreteType).countLength(content);
		if(cnt<minLength)
			return new DataTypeErrorDiagnosis( this, content, -1,
				DataTypeErrorDiagnosis.ERR_MINLENGTH,
				new Integer(cnt), new Integer(minLength) );
		
		return null;
	}
}
