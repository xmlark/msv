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
public class PatternFacet
{
	/** actual object that performs regular expression validation.
	 *
	 * one of the item has to match
	 */
	final private RegularExpression[] exps;

	/** a flag that indicates pattern facet is fixed and therefore
	 *  no further derivation is possible
	 */
	final private boolean isFixed;
	
	/** PatternFacet of base type. can be null */
	final private PatternFacet base;
		
	
	
	/**
	 * @param regularExpressions
	 *		Vector of XMLSchema-compiliant regular expression
	 *		(see http://www.w3.org/TR/xmlschema-2/#dt-regex )
	 *		There patterns are considered as an 'OR' set.
	 */
	public PatternFacet( PatternFacet base, Vector regExps, boolean isFixed )
		throws BadTypeException
	{
		// TODO : am I supposed to implement my own regexp validator?
		// at this time, I use Xerces' one.
		
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
		
		this.base = base;
		this.isFixed = isFixed;
	}
	
	/**
	 * checks if 'content' matchs this pattern
	 * 
	 * @return true if 'content' is accepted by this pattern
	 */
	public boolean verify( String lexicalValue )
	{
		if(base!=null)
			if(!base.verify(lexicalValue))
				return false;	// chain of ExpSet is considered as AND set

		for( int i=0; i<exps.length; i++ )
			if(exps[i].matches(lexicalValue))
				return true;

		return false;
	}
	
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
	public DataTypeErrorDiagnosis diagnose( String lexicalValue )
	{
		return null;
	}
	
	/**
	 * returns a PatternFacet object if "pattern" facet is specified.
	 * Otherwise returns null.
	 */
	public static PatternFacet create( Facets facets )
		throws BadTypeException
	{
		return merge(null,facets);
	}
	
	public static PatternFacet merge( PatternFacet base, Facets facets )
		throws BadTypeException
	{
		// if no pattern facet is specified
		if( !facets.contains("pattern") )
			return base;
		
		// makes sure that further derivation is allowed
		if( base!=null && base.isFixed )
			throw new BadTypeException(
				BadTypeException.ERR_OVERRIDING_FIXED_FACET, "pattern" );
		
		PatternFacet r = new PatternFacet(
			base!=null?base:null,
			facets.getVector("pattern"),
			facets.isFixed("pattern") );
		
		facets.consume("pattern");
		return r;
	}
}
