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
	/** singleton access to the plain ENTITY type */
	public static EntityType theInstance =
		new EntityType("ENTITY",null,null,null);
	
	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;
		if( lengths!=null && !lengths.verify(UnicodeUtil.countLength(content)))	return false;
		
		// ENTITY is a special case that lexical space is exactly the same as value space.
		// so we don't need to call convertValue method here.
		if( enumeration!=null && !enumeration.verify(content) )	return false;
		
		// we have to perform actual check
		throw new UnsupportedOperationException();
		
		// return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	public Object convertValue( String lexicalValue )
	{// for ENTITY, lexical space is value space by itself
		return lexicalValue;
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new EntityType( newName,
			LengthFacet.merge(this.lengths,facets),
			PatternFacet.merge(this.pattern,facets),
			EnumerationFacet.merge(this,this.enumeration,facets) );
	}
	
	private final LengthFacet			lengths;
//	private final RangeFacet			range;
	private final PatternFacet			pattern;
	private final EnumerationFacet		enumeration;
	
	/**
	 * constructor for derived-type from ENTITY by restriction.
	 * 
	 * To derive a datatype by restriction from ENTITY, call derive method.
	 * This method is only accessible within this class.
	 */
	private EntityType( String typeName, 
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration )
	{
		super( typeName );
		this.lengths	= lengths;
		this.pattern	= pattern;
		this.enumeration= enumeration;
	}
	
}
