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

import java.util.Hashtable;

/**
 * "string" and string-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#string for the spec
 */
public class StringType extends DataTypeImpl
{
	/** singleton access to the plain string type */
	public static StringType theInstance = new StringType("string");
	
	public boolean verify( String content )
	{
		// if base type exists, verify lexical value with the base type
		if( baseType!=null & !baseType.verify(content) )	return false;
		
		// performs whitespace pre-processing
		if( whiteSpace!=null )	content = whiteSpace.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;
		if( lengths!=null && !lengths.verify(UnicodeUtil.countLength(content)))	return false;
		
		// string is a special case that lexical space is exactly the same as value space.
		// so we don't need to call convertValue method here.
		if( enumeration!=null && !enumeration.verify(content) )	return false;
		
		// XML parser should already checked that
		// the lexical value is actually a sequence of characters
		// specified in http://www.w3.org/TR/REC-xml#NT-Char
		
		// so no further check is necessary
		
		return true;
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	public Object convertValue( String lexicalValue )
	{// for string, lexical space is value space by itself
		return lexicalValue;
	}
	
	public DataType derive( String newName, Hashtable facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.size()==0 )		return this;

		return new StringType( newName,
							   LengthFacet.merge(this,facets),
							   PatternFacet.merge(this,facets),
							   EnumerationFacet.create(this,facets),
							   WhiteSpaceProcessor.create(facets),
							   this );
	}
	
	private final LengthFacet lengths;
	private final PatternFacet pattern;
	private final EnumerationFacet enumeration;
	private final WhiteSpaceProcessor whiteSpace;
	private final DataType baseType;
	
	/**
	 * creates a plain string type which is specified in
	 * http://www.w3.org/TR/xmlschema-2/#string
	 * 
	 * This method is only accessible within this class.
	 * To use a plain string type, use theInstance property instead.
	 */
	protected StringType( String typeName )
	{
		super( typeName );
		lengths		= null;
		pattern		= null;
		enumeration	= null;
		whiteSpace	= null;
		baseType	= null;
	}
	
	/**
	 * constructor for derived-type from string by restriction.
	 * 
	 * To derive a datatype by restriction from string, call derive method.
	 * This method is only accessible within this class.
	 */
	protected StringType( String typeName, 
					    LengthFacet lengths, PatternFacet pattern,
						EnumerationFacet enumeration, WhiteSpaceProcessor whiteSpace,
						DataType baseType )
	{
		super( typeName );
		this.lengths	= lengths;
		this.pattern	= pattern;
		this.enumeration= enumeration;
		this.whiteSpace	= whiteSpace;
		this.baseType	= baseType;
	}
	
}
