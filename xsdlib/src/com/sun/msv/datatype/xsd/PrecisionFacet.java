package com.sun.tranquilo.datatype;

/**
 * 'precision' facet.
 *
 * this class holds these facet information and performs validation.
 */
class PrecisionFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of total digits. */
	protected final int		precision;

	public PrecisionFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_PRECISION, facets );
		
		precision = facets.getPositiveInteger(FACET_PRECISION);
		
		facets.consume( FACET_PRECISION );
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_PRECISION);
		if(o!=null && ((PrecisionFacet)o).precision < this.precision )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_PRECISION, o.getName() );
		
		// consistency with scale is checked in DataTypeImpl.derive method.
	}

	protected boolean checkLexicalConstraint( String literal )
	{
		// count the number of digits.
		final int len = literal.length();
		boolean skipMode = true;
		char[] chs = literal.toCharArray();

		int count=0;
		
		for( int i=0; i<len; i++ )
			if( skipMode )
			{
				if( '0'<chs[i] && chs[i]<='9' )
				{
					count++;
					skipMode = false;
				}
			}
			else
			{
				if( '0'<chs[i] && chs[i]<='9' )
					count++;
			}
		
		return count<=precision;
	}
}
