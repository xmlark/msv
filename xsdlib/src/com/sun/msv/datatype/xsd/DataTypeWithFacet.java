package com.sun.tranquilo.datatype;

abstract class DataTypeWithFacet extends DataTypeImpl
{
	protected final DataTypeImpl baseType;
	
	/** name of this facet */
	private final String facetName;
	
	/** a flag that indicates the facet is fixed (derived types cannot specify this value anymore) */
	protected final boolean isFacetFixed;
	
	/** a flag that indicates this type has value-constraint facet.
	 * 
	 * this value is used to cache this flag.
	 */
	private final boolean needValueCheckFlag;
	
	/** constructor for facets other than WhiteSpaceFacet */
	DataTypeWithFacet( String typeName, DataTypeImpl baseType, String facetName, Facets facets )
		throws BadTypeException
	{
		this( typeName, baseType, facetName, facets, baseType.whiteSpace );
	}
	
	/** constructor for WhiteSpaceFacet */
	DataTypeWithFacet( String typeName, DataTypeImpl baseType, String facetName, Facets facets, WhiteSpaceProcessor whiteSpace )
		throws BadTypeException
	{
		super(typeName, whiteSpace);
		this.baseType = baseType;
		this.facetName = facetName;
		this.isFacetFixed = facets.isFixed(facetName);
		
		needValueCheckFlag = baseType.needValueCheck();
		
		int r = baseType.isFacetApplicable(facetName);
		switch(r)
		{
		case APPLICABLE:	return;	// this facet is applicable to this type. no problem.
		case NOT_ALLOWED:
			throw new BadTypeException( BadTypeException.ERR_NOT_APPLICABLE_FACET, facetName );
		case FIXED:
			throw new BadTypeException( BadTypeException.ERR_OVERRIDING_FIXED_FACET, facetName );
		}
	}
	
	final public int isFacetApplicable( String facetName )
	{
		if( this.facetName.equals(facetName) )
		{
			if( isFacetFixed )		return FIXED;
			else					return APPLICABLE;
		}
		else
			return baseType.isFacetApplicable(facetName);
	}
	
	protected boolean needValueCheck() { return needValueCheckFlag; }
}
