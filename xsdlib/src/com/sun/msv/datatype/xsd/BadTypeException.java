/*
 * Tranquilo : RELAX Verifier           written by Kohsuke Kawaguchi
 *                                           k-kawa@bigfoot.com
 *
 * Copyright 2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package com.sun.tranquilo.datatype;

/**
 * Exception
 *
 * This exception is thrown when there is an error
 * regarding type definition (mainly derivation).
 */
public class BadTypeException extends Exception
{
	public BadTypeException(String resourcePropertyName,Object[] args)
	{
		super( java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.tranquilo.datatype.Messages").getString(resourcePropertyName),
			args ) );
	}

	public BadTypeException(String resourcePropertyName,Object arg1,Object arg2,Object arg3)
	{
		this( resourcePropertyName, new Object[]{arg1,arg2,arg3} );
	}
	
	public BadTypeException(String resourcePropertyName,Object arg1,Object arg2)
	{
		this( resourcePropertyName, new Object[]{arg1,arg2} );
	}
	
	public BadTypeException(String resourcePropertyName,Object arg1)
	{
		this( resourcePropertyName, new Object[]{arg1} );
	}

	public BadTypeException(String resourcePropertyName)
	{
		this( resourcePropertyName, null );
	}
	
	public static final String ERR_INVALID_ITEMTYPE = null;
	// Type "{0}" must be an atom type in order to be used as an item type
	// of list.
	
	public static final String ERR_INVALID_WHITESPACE_VALUE =
		"WhiteSpaceProcessor.InvalidWhiteSpaceValue";
	public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
	
	public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE =
		"EnumerationFacet.InvalidValueForThisType";
	public final static String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER
		= "BadTypeException.FacetMustBeNonNegativeInteger";
	public final static String ERR_FACET_MUST_BE_POSITIVE_INTEGER
		= null; // facet "{0}" must be a positive integer value
	public final static String ERR_OVERRIDING_FIXED_FACET
		= null;	// facet "{0}" is specified as fixed in base type.
	public final static String ERR_INCONSISTENT_FACETS_1
		= "InconsistentFacets.1";
	public final static String ERR_INCONSISTENT_FACETS_2
		= "InconsistentFacets.2";
	public final static String ERR_X_AND_Y_ARE_EXCLUSIVE
		= "XAndYAreExclusive";
	public final static String ERR_LOOSENED_FACET
		= "LoosenedFacet";
	public final static String ERR_SCALE_IS_GREATER_THAN_PRECISION =
		"PrecisionScaleFacet.ScaleIsGraterThanPrecision";
	public final static String ERR_EXCLUSIVE_FACETS_SPECIFIED =
		"RangeFacet.ExclusiveFacetsSpecified";
	public final static String ERR_ILLEGAL_MAX_MIN_ORDER =
		"RangeFacet.IllegalMaxMinOrder";
	public final static String ERR_INAPPROPRIATE_VALUE_FOR_X =
		"RangeFacet.IllegalValueForX";
	public static final String ERR_DUPLICATE_FACET
		= null; // facet {0} is specified more than once.
	public static final String ERR_UNCONSUMED_FACET
		= null; // unrecognized facet specification {0}
	public static final String ERR_NOT_APPLICABLE_FACET
		= null; // facet {0} is not applicable to this datatype
	public static final String ERR_EMPTY_UNION
		= "BadTypeException.EmptyUnion";

}
