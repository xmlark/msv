/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.verifier.Acceptor;

/**
 * Accept that is used when more than one pattern can be applicable to the current context.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ComplexAcceptor extends ComplexAcceptorBaseImpl
{
	public final ElementExp[]	owners;
	
	public ComplexAcceptor(
		REDocumentDeclaration docDecl,
		Expression combined,
		Expression[] contentModels, ElementExp[] owners )
	{
		super( docDecl, combined, contentModels );
		this.owners = owners;
	}

	/**
	 * collects satisfied ElementExps.
	 * 
	 * "satisfied ElementExps" are ElementExps whose
	 * contents is now epsilon reducible.
	 */
	public final ElementExp[] getSatisfiedOwners()
	{
		ElementExp[] satisfied;
		
		int i,cnt;
		// count # of satisfied ElementExp.
		for( i=0,cnt=0; i<contents.length; i++ )
			if( contents[i].isEpsilonReducible() )	cnt++;
			
		if(cnt==0)	return new ElementExp[0];	// no one is satisfied.
			
		satisfied = new ElementExp[cnt];
		for( i=0,cnt=0; i<contents.length; i++ )
			if( contents[i].isEpsilonReducible() )
				satisfied[cnt++] = owners[i];
		
		return satisfied;
	}
}
