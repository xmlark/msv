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
 * "boolean" and boolean-derived types
 * 
 * See http://www.w3.org/TR/xmlschema-2/#boolean for the spec
 */
public class BooleanType extends DataTypeImpl
{
	/** singleton access to the plain string type */
	public static BooleanType theInstance = new BooleanType("boolean",null);

	public boolean verify( String content )
	{
		// performs whitespace pre-processing
		content = WhiteSpaceProcessor.theCollapse.process(content);
		
		// checks additional facets
		if( pattern!=null && !pattern.verify(content) )		return false;

		// checks the lexical value
		return "true".equals(content) || "false".equals(content);
	}
	
	public DataTypeErrorDiagnosis diagnose( String content )
	{
		// TODO : implement this method
		return null;
	}
	
	public Object convertValue( String lexicalValue )
		throws ConvertionException
	{// for string, lexical space is value space by itself
		if( lexicalValue.equals("true") )		return new Boolean(true);
		if( lexicalValue.equals("false") )		return new Boolean(false);
		throw new ConvertionException();
	}
	
	public DataType derive( String newName, Facets facets )
		throws BadTypeException
	{
		// no facets specified. So no need for derivation
		if( facets.isEmpty() )		return this;

		return new BooleanType( newName,
								PatternFacet.merge(this.pattern,facets)
								);
	}

	private final PatternFacet pattern;
	
	/**
	 * constructor for derived-type from boolean by restriction.
	 * 
	 * To derive a datatype by restriction from boolean, call derive method.
	 * This method is only accessible within this class.
	 */
	private BooleanType( String typeName, PatternFacet pattern )
	{
		super( typeName );
		this.pattern	= pattern;
	}
	
}
