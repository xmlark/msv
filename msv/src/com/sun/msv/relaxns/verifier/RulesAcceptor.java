/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.relaxns.verifier;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.regexp.trex.TREXDocumentDeclaration;
import com.sun.msv.datatype.ValidationContextProvider;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.util.StringRef;
import com.sun.msv.util.DataTypeRef;
import org.iso_relax.dispatcher.ElementDecl;

/**
 * Acceptor that is used to validate root node of the island.
 * 
 * This object receives {@link RuleImpl}s and validates them.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RulesAcceptor
	extends com.sun.msv.verifier.regexp.trex.ComplexAcceptorBaseImpl
{
	protected final DeclImpl[]		owners;
	
	/** helper function for constructor */
	private static Expression createCombined( ExpressionPool pool, DeclImpl[] rules )
	{
		Expression exp = Expression.nullSet;
		for( int i=0; i<rules.length; i++ )
			exp = pool.createChoice( exp, rules[i].exp );
		return exp;
	}
	
	/** helper function for constructor */
	private static Expression[] getContents( DeclImpl[] rules )
	{
		Expression[] r = new Expression[rules.length];
		for( int i=0; i<rules.length; i++ )
			r[i] = rules[i].exp;
		return r;
	}
	
	public RulesAcceptor(
		TREXDocumentDeclaration docDecl,
		DeclImpl[] rules )
	{
		super( docDecl, createCombined(docDecl.getPool(),rules), getContents(rules) );
		owners = rules;
	}
	
	/**
	 * collects satisfied ElementDeclImpls.
	 * 
	 * @see com.sun.msv.verifier.regexp.trex.ComplexAcceptor#getSatisfiedOwners
	 */
	ElementDecl[] getSatisfiedElementDecls()
	{
		int cnt=0;
		for( int i=0; i<owners.length; i++ )
			if( contents[i].isEpsilonReducible() )
				cnt++;
		
		ElementDecl[] r = new DeclImpl[cnt];
		cnt=0;
		for( int i=0; i<owners.length; i++ )
			if( contents[i].isEpsilonReducible() )
				r[cnt++] = owners[i];
		
		return r;
	}
}
