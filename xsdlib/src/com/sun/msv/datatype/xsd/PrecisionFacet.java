package com.sun.tranquilo.datatype;

/**
 * 'precision' facet.
 *
 * this class holds these facet information and performs validation.
 */
class PrecisionFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of total digits. -1 if unspecified. */
	private final int		precision;

	public PrecisionFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_PRECISION, facets );
		
		precision = facets.getPositiveInteger(FACET_PRECISION);
		
		facets.consume( FACET_PRECISION );
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
