/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/**
 * 'totalDigits' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author Kohsuke KAWAGUCHI
 */
class TotalDigitsFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of total digits. */
	protected final int		precision;

	public TotalDigitsFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_TOTALDIGITS, facets );
		
		precision = facets.getPositiveInteger(FACET_TOTALDIGITS);
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_TOTALDIGITS);
		if(o!=null && ((TotalDigitsFacet)o).precision < this.precision )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_TOTALDIGITS, o.displayName() );
		
		// consistency with scale is checked in DataTypeImpl.derive method.
	}

	protected boolean checkLexicalConstraint( String content )
	{
		return countPrecision(content)<=precision;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		final int cnt = countPrecision(content);
		if( cnt<=precision )
			return null;
		
		return new DataTypeErrorDiagnosis(this, content, -1,
			localize(ERR_TOO_MUCH_PRECISION, new Integer(cnt), new Integer(precision)) );
	}
	
	/** counts the number of digits */
	protected static int countPrecision( String literal )
	{
		final int len = literal.length();
		boolean skipMode = true;

		int count=0;
		int trailingZero=0;
		
		for( int i=0; i<len; i++ )
		{
			final char ch = literal.charAt(i);
			if( skipMode )
			{// in skip mode, leading zeros are skipped
				if( '1'<=ch && ch<='9' )
				{
					count++;
					skipMode = false;
				}
				if( '.'==ch )	// digits after '.' is considered significant.
					skipMode = false;
			}
			else
			{
				if( ch=='0' )	trailingZero++;
				else
				if( ch=='.' )	;	// do nothing
				else			trailingZero=0;
				
				if( '0'<=ch && ch<='9' )
					count++;
			}
		}
		
		return count-trailingZero;
	}
}
