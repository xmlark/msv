package com.sun.tranquilo.datatype;

/**
 * 'scale' facet.
 *
 * this class holds these facet information and performs validation.
 */
class ScaleFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of fraction digits */
	private final int scale;

	public ScaleFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_SCALE, facets );
		
		scale = facets.getNonNegativeInteger(FACET_SCALE);
		
		facets.consume( FACET_SCALE );
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
				if( chs[i]=='.' )
					skipMode = false;
			}
			else
			{
				if( '0'<chs[i] && chs[i]<='9' )
					count++;
			}
		
		return count<=scale;
	}
}
