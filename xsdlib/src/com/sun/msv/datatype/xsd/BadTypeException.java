/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.datatype;

/**
 * signals bad type definition.
 *
 * This exception is thrown when there is an error
 * regarding type definition (mainly derivation).
 * 
 * @author	Kohsuke Kawaguchi
 */
public class BadTypeException extends Exception {
	
	public BadTypeException(String resourcePropertyName,Object[] args) {
		super( java.text.MessageFormat.format(
			java.util.ResourceBundle.getBundle("com.sun.msv.datatype.Messages").getString(resourcePropertyName),
			args ) );
	}

	public BadTypeException(String resourcePropertyName,Object arg1,Object arg2,Object arg3) {
		this( resourcePropertyName, new Object[]{arg1,arg2,arg3} );
	}
	
	public BadTypeException(String resourcePropertyName,Object arg1,Object arg2) {
		this( resourcePropertyName, new Object[]{arg1,arg2} );
	}
	
	public BadTypeException(String resourcePropertyName,Object arg1) {
		this( resourcePropertyName, new Object[]{arg1} );
	}

	public BadTypeException(String resourcePropertyName) {
		this( resourcePropertyName, null );
	}
	
	public static final String ERR_INVALID_ITEMTYPE =
		"BadTypeException.InvalidItemType";
	public static final String ERR_INVALID_MEMBER_TYPE =
		"BadTypeException.InvalidMemberType";
	public static final String ERR_INVALID_BASE_TYPE =
		"BadTypeException.InvalidBaseType";
	public static final String ERR_INVALID_WHITESPACE_VALUE =
		"WhiteSpaceProcessor.InvalidWhiteSpaceValue";
	public static final String ERR_PARSE_ERROR = "PatternFacet.ParseError";
	
	public static final String ERR_INVALID_VALUE_FOR_THIS_TYPE =
		"EnumerationFacet.InvalidValueForThisType";
	public final static String ERR_FACET_MUST_BE_NON_NEGATIVE_INTEGER =
		"BadTypeException.FacetMustBeNonNegativeInteger";
	public final static String ERR_FACET_MUST_BE_POSITIVE_INTEGER =
		"BadTypeException.FacetMustBePositiveInteger";
	public final static String ERR_OVERRIDING_FIXED_FACET =
		"BadTypeException.OverridingFixedFacet";
	public final static String ERR_INCONSISTENT_FACETS_1 =
		"InconsistentFacets.1";
	public final static String ERR_INCONSISTENT_FACETS_2 =
		"InconsistentFacets.2";
	public final static String ERR_X_AND_Y_ARE_EXCLUSIVE =
		"XAndYAreExclusive";
	public final static String ERR_LOOSENED_FACET =
		"LoosenedFacet";
	public final static String ERR_SCALE_IS_GREATER_THAN_PRECISION =
		"PrecisionScaleFacet.ScaleIsGraterThanPrecision";
	public static final String ERR_DUPLICATE_FACET =
		"BadTypeException.DuplicateFacet";
	public static final String ERR_NOT_APPLICABLE_FACET =
		"BadTypeException.NotApplicableFacet";
	public static final String ERR_EMPTY_UNION =
		"BadTypeException.EmptyUnion";
}
