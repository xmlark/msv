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
				FACET_TOTALDIGITS, o.getName() );
		
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
			DataTypeErrorDiagnosis.ERR_TOO_MUCH_PRECISION, new Integer(cnt), new Integer(precision) );
	}
	
	/** counts the number of digits */
	private final int countPrecision( String literal )
	{
		// count the number of digits.
		final int len = literal.length();
		boolean skipMode = true;
		char[] chs = literal.toCharArray();

		int count=0;
		
		for( int i=0; i<len; i++ )
			if( skipMode )
			{// in skip mode, leading zeros are skipped
				if( '0'<chs[i] && chs[i]<='9' )
				{
					count++;
					skipMode = false;
				}
			}
			else
			{
				if( '0'<=chs[i] && chs[i]<='9' )
					count++;
			}
		
		return count;
	}
}
