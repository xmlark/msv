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

import org.apache.xerces.utils.regex.RegularExpression;
import org.apache.xerces.utils.regex.ParseException;
import java.util.Vector;

/**
 * "pattern" facet validator
 * 
 * "pattern" is a constraint facet which is applied against lexical space.
 * See http://www.w3.org/TR/xmlschema-2/#dt-pattern for the spec
 */
final class PatternFacet extends DataTypeWithLexicalConstraintFacet
{
	/** actual object that performs regular expression validation.
	 *
	 * one of the item has to match
	 */
	final private RegularExpression[] exps;

	
	
	/**
	 * @param regularExpressions
	 *		Vector of XMLSchema-compiliant regular expression
	 *		(see http://www.w3.org/TR/xmlschema-2/#dt-regex )
	 *		There patterns are considered as an 'OR' set.
	 */
	public PatternFacet( String typeName, DataTypeImpl baseType, TypeIncubator facets )
		throws BadTypeException
	{
		super( typeName, baseType, FACET_PATTERN, facets );
		
		
		// TODO : am I supposed to implement my own regexp validator?
		// at this time, I use Xerces' one.
		
		Vector regExps = facets.getVector(FACET_PATTERN);
		
		exps = new RegularExpression[regExps.size()];
		try
		{
			for(int i=0;i<regExps.size();i++)
				exps[i] = new RegularExpression((String)regExps.elementAt(i),"X");
		}
		catch( ParseException pe )
		{// in case regularExpression is not a correct pattern
			throw new BadTypeException(
				BadTypeException.ERR_PARSE_ERROR,
				pe.getMessage() );
		}
		
		// loosened facet check is almost impossible for pattern facet.
		// ignore it for now.
	}
	
	protected DataTypeErrorDiagnosis diagnoseByFacet(String content, ValidationContextProvider context)
	{
		if( checkLexicalConstraint(content) )	return null;
		
		if( exps.length==1 )
			return new DataTypeErrorDiagnosis( this, content, -1,
				DataTypeErrorDiagnosis.ERR_PATTERN_1, exps[0] );
		else
			return new DataTypeErrorDiagnosis( this, content, -1,
				DataTypeErrorDiagnosis.ERR_PATTERN_MANY );
	}
	
	protected final boolean checkLexicalConstraint( String literal )
	{
		// makes sure that at least one of the patterns is satisfied.
		for( int i=0; i<exps.length; i++ )
			if(exps[i].matches(literal))
				return true;
		// otherwise fail
		return false;
	}
}
