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
 * 'fractionDigits' facet.
 *
 * this class holds these facet information and performs validation.
 * 
 * @author Kohsuke KAWAGUCHI
 */
class FractionDigitsFacet extends DataTypeWithLexicalConstraintFacet
{
	/** maximum number of fraction digits */
	protected final int scale;

	public FractionDigitsFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_FRACTIONDIGITS, facets );
		
		scale = facets.getNonNegativeInteger(FACET_FRACTIONDIGITS);
		
		// loosened facet check
		DataTypeWithFacet o = baseType.getFacetObject(FACET_FRACTIONDIGITS);
		if(o!=null && ((FractionDigitsFacet)o).scale < this.scale )
			throw new BadTypeException(
				BadTypeException.ERR_LOOSENED_FACET,
				FACET_FRACTIONDIGITS, o.displayName() );
		
		// consistency with precision is checked in DataTypeImpl.derive method.
	}

	protected boolean checkLexicalConstraint( String content )
	{
		return countScale(content)<=scale;
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		final int cnt = countScale(content);
		if(cnt<=scale)		return null;
		
		return new DataTypeErrorDiagnosis( this, content, -1, 
			localize(ERR_TOO_MUCH_SCALE,
			new Integer(cnt), new Integer(scale)) );
	}
	
	/** count the number of fractional digits.
	 * 
	 * this method can assume that the given literal is appropriate
	 * as an decimal value.
	 * 
	 * "the number of fractional digits" is defined in
	 * http://www.w3.org/TR/xmlschema-2/#number
	 */
	final protected static int countScale( String literal )
	{
		final int len = literal.length();
		boolean skipMode = true;

		int count=0;
		int trailingZero=0;
		
		for( int i=0; i<len; i++ )
		{
			final char ch = literal.charAt(i);
			if( skipMode )
			{
				if( ch=='.' )
					skipMode = false;
			}
			else
			{
				if( ch=='0' )	trailingZero++;
				else			trailingZero=0;
				
				if( '0'<=ch && ch<='9' )
					count++;
			}
		}
		
		return count-trailingZero;
	}
}
