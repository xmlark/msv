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
	 * checks if 'content' matchs this datatype
	 * 
	 * @return true if 'content' can is a member of this datatype
	 */
	boolean verify( String content );
	
	/**
	 * computes the reason of error
	 * 
	 * Application can call this method to provide detailed error message to user.
	 * This method is kept separate from verify method to achieve higher performance
	 * if no such message is necessary at all.
	 * 
	 * @return null
	 *		if 'content' is accepted by this pattern, or 
	 *		if the derived class doesn't support this operation
	 */
//	DataTypeErrorDiagnosis diagnose( String content )
//		throws java.lang.UnsupportedOperationException;
	
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
	 */
	DataType derive( String newTypeName, Facets facets )
		throws BadTypeException;
	
	
	/**
	 * converts lexcial value to the corresponding value object of the value space
	 * 
	 * @return	null
	 *		when the given lexical value is not valid lexical value for this type.
	 */
	Object convertToValueObject( String lexicalValue );
	
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
