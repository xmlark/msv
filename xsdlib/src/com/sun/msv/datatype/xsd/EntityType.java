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
 * "ENTITY" and ENTITY-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#ENTITY for the spec
 */
public class EntityType extends DataTypeImpl
{
	public static final EntityType theInstance = new EntityType();
	private EntityType() { super("ENTITY"); }
	
	public final int isFacetApplicable( String facetName )
	{
		// TODO : should we allow scale facet, or not?
		if( facetName.equals(FACET_LENGTH)
		||	facetName.equals(FACET_MINLENGTH)
		||	facetName.equals(FACET_MAXLENGTH)
		||	facetName.equals(FACET_PATTERN)
		||	facetName.equals(FACET_ENUMERATION) )
			return APPLICABLE;
		else
			return NOT_ALLOWED;
	}
	
	protected boolean checkFormat( String content )
	{
		// we have to perform actual check
		throw new UnsupportedOperationException();
	}

	public Object convertToValue( String content )
	{
		// we have to perform actual check
		throw new UnsupportedOperationException();
	}
}
