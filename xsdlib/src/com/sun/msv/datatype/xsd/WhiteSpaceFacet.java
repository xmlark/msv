package com.sun.tranquilo.datatype;

public class WhiteSpaceFacet extends DataTypeWithFacet
{
	WhiteSpaceFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super(typeName, baseType, FACET_WHITESPACE, facets,
			WhiteSpaceProcessor.get(facets.getFacet(FACET_WHITESPACE)) );
		
		// loosened facet check
		if( baseType.whiteSpace.tightness() > this.whiteSpace.tightness() )
		{
			DataType d;
			d=baseType.getFacetObject(FACET_WHITESPACE);
			if(d==null)	d = getConcreteType();
			
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_LENGTH, d.getName() );
		}
		
		// consistency with minLength/maxLength is checked in DataTypeImpl.derive method.
		
		facets.consume(FACET_WHITESPACE);
	}
	
	protected boolean checkFormat( String content, ValidationContextProvider context )
	{ return baseType.checkFormat(content,context); }
	public Object convertToValue( String content, ValidationContextProvider context )
	{ return baseType.convertToValue(content,context); }
	
	/** whiteSpace facet never constrain anything */
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{ return null; }
}
