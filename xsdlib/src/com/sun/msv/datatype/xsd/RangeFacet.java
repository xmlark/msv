package com.sun.tranquilo.datatype;

/**
 * Base class of "(max|min)(In|Ex)clusive" facet validator
 */
abstract class RangeFacet extends DataTypeWithValueConstraintFacet
{
	protected final Object limitValue;

	protected RangeFacet( String typeName, DataTypeImpl baseType, String facetName, Facets facets )
		throws BadTypeException
	{
		super(typeName,baseType,facetName,facets);
		
		limitValue = baseType.convertToValueObject( facets.getFacet(facetName) );
		if( limitValue==null )
			throw new BadTypeException(
				BadTypeException.ERR_INAPPROPRIATE_VALUE_FOR_X,
				facets.getFacet(facetName), facetName );
			
		facets.consume(facetName);
		
		// TODO : consistency check of RangeFacet
	}
	
	public final Object convertToValue( String literal )
	{
		Object o = baseType.convertToValue(literal);
		if(o==null)	return null;
		
		int r = ((Comparator)concreteType).compare(limitValue,o);
		if(!rangeCheck(r))		return null;
		return o;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content)
	{
		if( convertToValue(content)!=null )		return null;
			
		return new DataTypeErrorDiagnosis(this, content, -1,
			DataTypeErrorDiagnosis.ERR_OUT_OF_RANGE, facetName, limitValue );
	}
	
	
	protected abstract boolean rangeCheck( int compareResult );
}
