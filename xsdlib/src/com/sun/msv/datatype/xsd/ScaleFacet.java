package com.sun.tranquilo.datatype;

/**
 * 'scale' facet.
 *
 * this class holds these facet information and performs validation.
 */
class ScaleFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of fraction digits */
	protected final int scale;

	public ScaleFacet( String typeName, DataTypeImpl baseType, Facets facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_SCALE, facets );
		
		scale = facets.getNonNegativeInteger(FACET_SCALE);
		
		facets.consume( FACET_SCALE );
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_SCALE);
		if(o!=null && ((ScaleFacet)o).scale < this.scale )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_SCALE, o.getName() );
		
		// consistency with precision is checked in DataTypeImpl.derive method.
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
