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

import java.io.Serializable;
import java.lang.Cloneable;
import java.util.Hashtable;

/**
 * Publicly accesible interface of XSD Datatype.
 *
 * Application should use this interface to interact with datatype objects.
 * 
 * @author	Kohsuke Kawaguchi
 */
public interface DataType extends Serializable,Cloneable
{
	/**
	 * checks if 'literal' matchs this datatype.
	 * 
	 * @param literal
	 *		the lexical representation to be verified
	 * @param context
	 *		context information provider that may be
	 *		necessary to verify given literal.
	 * 
	 * @return
	 *		true if 'literal' is a member of this datatype;
	 *		false if it's not a member of this datatype.
	 */
	boolean verify( String literal, ValidationContextProvider context );
	
	/**
	 * diagnoses the reason of error.
	 * 
	 * Application can call this method to provide detailed error message to users.
	 * This method is kept separate from verify method to achieve higher performance
	 * during normal validation. This method is optional and may not be implemented
	 * by some of datatypes.
	 * 
	 * @return null
	 *		if 'content' is accepted by this pattern.
	 * 
	 * @exception UnsupportedOperationException
	 *		if diagnosis is not supported by the implementation.
	 */
	DataTypeErrorDiagnosis diagnose( String content, ValidationContextProvider context )
		throws UnsupportedOperationException;
	
	/**
	 * gets the name of this datatype.
	 * 
	 * Anonymous datatypes must return null.
	 * As a result, result of this method cannot be used as
	 * an unique identifier of type, nor a display name.
	 */
	String getName();
	
	/**
	 * gets type name suitable for display.
	 * 
	 * Non-anonymous datatypes should return the same value as getName() method.
	 * Anonymous types should return non-null string.
	 */
	String displayName();
	
	
	/**
	 * converts lexcial value to the corresponding value object of the value space.
	 * type of the value object depends on implementation.
	 * 
	 * @return	null
	 *		when the given lexical value is not valid lexical value for this type.
	 */
	Object convertToValueObject( String lexicalValue, ValidationContextProvider context );
	
	/**
	 * checks if this type is an atom type.
	 * 
	 * List, union, and types derived from them are not atom types.
	 * Other types are atom types.
	 *
	 * @returns true if this type is an atom type
	 */
	boolean isAtomType();
	
	/** checks if this type is declared as final for the specified kind of derivation.
	 * 
	 * @param derivationType
	 *		one of pre-defined values
	 */
	boolean isFinal( int derivationType );
	
	public static final int DERIVATION_BY_RESTRICTION		= 0x01;
	public static final int DERIVATION_BY_LIST				= 0x02;
	public static final int DERIVATION_BY_UNION				= 0x04;

	/**
	 * indicates the specified facet is applicable to this type.
	 * One of the possible return value from isFacetApplicable method.
	 */
	static final int APPLICABLE = 0;
	/**
	 * indicates the specified facet is fixed in this type and
	 * therefore not appliable.
	 * One of the possible return value from isFacetApplicable method.
	 */
	static final int FIXED		= -1;
	/**
	 * indicates the specified facet is not appliable to this type by definition.
	 * One of the possible return value from isFacetApplicable method.
	 */
	static final int NOT_ALLOWED= -2;
	/**
	 * returns if the specified facet is applicable to this datatype.
	 * 
	 * @return	APPLICABLE		if the facet is applicable;
	 *			FIXED			if the facet is already fixed (that is,not applicable);
	 *			NOT_ALLOWED		if the facet is not applicable to this datatype at all.
	 *							this value is also returned for unknown facets.
	 */
	public int isFacetApplicable( String facetName );


	// well-known facet name constants
	final static String	FACET_LENGTH			= "length";
	final static String	FACET_MINLENGTH			= "minLength";
	final static String	FACET_MAXLENGTH			= "maxLength";
	final static String	FACET_PATTERN			= "pattern";
	final static String	FACET_ENUMERATION		= "enumeration";
	final static String	FACET_TOTALDIGITS		= "totalDigits";
	final static String	FACET_FRACTIONDIGITS	= "fractionDigits";
	final static String	FACET_MININCLUSIVE		= "minInclusive";
	final static String	FACET_MAXINCLUSIVE		= "maxInclusive";
	final static String	FACET_MINEXCLUSIVE		= "minExclusive";
	final static String	FACET_MAXEXCLUSIVE		= "maxExclusive";
	final static String	FACET_WHITESPACE		= "whiteSpace";
}
