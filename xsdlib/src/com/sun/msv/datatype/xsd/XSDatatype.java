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

import java.io.Serializable;
import java.util.Hashtable;
import org.relaxng.datatype.ValidationContext;

/**
 * Publicly accesible interface of XSD Datatype.
 *
 * Application should use this interface to interact with datatype objects.
 * 
 * @author	Kohsuke Kawaguchi
 */
public interface XSDataType extends
	Serializable, org.relaxng.datatype.Datatype {

	/** gets the displayable name of this type. */
	String displayName();
	
	/**
	 * converts value object back to the corresponding value in the lexical space.
	 * 
	 * This method does the reverse operation of convertToValueObject.
	 * The returned string is not necessarily the canonical representation.
	 * 
	 * Also note that the implementation may accept invalid values without throwing
	 * IllegalArgumentException. To make sure that the result is actually a valid
	 * representation, call the verify method.
	 * 
	 * @param context
	 *		Context information provider that might be used for conversion.
	 *		Currently, this object is used only for QName, but may be extended
	 *		in the future.
	 * 
	 * @exception IllegalArgumentException
	 *		if the given object does not belong to the value space of this datatype.
	 */
	String convertToLexicalValue( Object valueObject, SerializationContext context ) throws IllegalArgumentException;

	
	/**
	 * converts lexcial value to a corresponding Java-friendly object
	 * by using the given context information.
	 * 
	 * <p>
	 * For the actual types returned by each type,
	 * see <a href="package-summary.html#javaType">here</a>.
	 * 
	 * <p>
	 * Note that due to the difference between those Java friendly types
	 * and actual XML Schema specification, the returned object sometimes
	 * loses accuracy,
	 * 
	 * @return	null
	 *		when the given lexical value is not a valid lexical value for this type.
	 */
	Object createJavaObject( String literal, ValidationContext context );
	
	/**
	 * gets the type of the objects that are created by the createJavaObject method.
	 */
	Class getJavaObjectType();
	
	
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



	
	/**
	 * gets the facet object that restricts the specified facet
	 *
	 * @return null
	 *		if no such facet object exists.
	 */
	public DataTypeWithFacet getFacetObject( String facetName );

	/**
	 * gets the concrete type object of the restriction chain.
	 */
	public ConcreteType getConcreteType();


}
