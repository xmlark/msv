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

import java.io.Serializable;
import java.lang.Cloneable;
import java.util.Hashtable;

/**
 * Base interfce of datatypes
 */
public interface DataType extends Serializable,Cloneable
{
	/**
	 * checks if 'literal' matchs this datatype
	 * 
	 * @param literal
	 *		the lexical representation to be verified
	 * @param context
	 *		context information provider that may be
	 *		necessary to verify given literal.
	 * 
	 * @return true if 'literal' can is a member of this datatype
	 */
	boolean verify( String literal, ValidationContextProvider context );
	
	/**
	 * computes the reason of error
	 * 
	 * Application can call this method to provide detailed error message to user.
	 * This method is kept separate from verify method to achieve higher performance
	 * during normal validation
	 * 
	 * @return null
	 *		if 'content' is accepted by this pattern, or 
	 *		if the derived class doesn't support this operation
	 */
	DataTypeErrorDiagnosis diagnose( String content, ValidationContextProvider context )
		throws UnsupportedOperationException;
	
	/**
	 * gets the name of this datatype
	 * 
	 * Unnamed datatypes (e.g., derived types) must return null.
	 * As a result, name of the datatype cannot be used as an identifier
	 */
	String getName();
	
	/**
	 * derives a new datatype from this datatype, by adding facets
	 * 
	 * It is completely legal to use null as the newTypeName paratmer,
	 * which means deriving anonymous datatype.
	 *
	 * @param context
	 *		in case of deriving a type from QName, the implementation needs to
	 *		resolve prefixs to namespace URIs. Therefore, the caller must
	 *		supply a ValidationContextProvider.
	 */
	DataType derive( String newTypeName, Facets facets, ValidationContextProvider context )
		throws BadTypeException;
	
	
	/**
	 * converts lexcial value to the corresponding value object of the value space
	 * 
	 * @return	null
	 *		when the given lexical value is not valid lexical value for this type.
	 */
	Object convertToValueObject( String lexicalValue, ValidationContextProvider context );
	
	/**
	 * @returns true if this type is an atom type
	 */
	boolean isAtomType();

	static final int APPLICABLE = 0;
	static final int FIXED		= -1;
	static final int NOT_ALLOWED= -2;
	/** returns if the specified facet is applicable to this datatype.
	 * 
	 * @return	APPLICABLE		if the facet is applicable
	 *			FIXED			if the facet is already fixed (that is,not applicable)
	 *			NOT_ALLOWED		if the facet is not applicable to this datatype at all.
	 */
	public int isFacetApplicable( String facetName );
}
