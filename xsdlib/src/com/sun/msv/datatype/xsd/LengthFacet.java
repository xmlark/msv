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
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_LENGTH);
		if(o!=null && ((LengthFacet)o).length != this.length )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_LENGTH, o.getName() );
		
		// consistency with minLength/maxLength is checked in DataTypeImpl.derive method.
	}
	
	public Object convertToValue( String content )
	{
		Object o = baseType.convertToValue(content);
		if(o==null || ((Discrete)concreteType).countLength(content)!=length)	return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content)
	{
		Object o = concreteType.convertToValue(content);
		// base type must have accepted this lexical value, otherwise 
		// this method is never called.
		if(o==null)	throw new IllegalStateException();	// assertion
		
		int cnt = ((Discrete)concreteType).countLength(content);
		if(cnt!=length)
			return new DataTypeErrorDiagnosis( this, content, -1,
				DataTypeErrorDiagnosis.ERR_LENGTH,
				new Integer(cnt), new Integer(length) );
		
		return null;
	}
}
