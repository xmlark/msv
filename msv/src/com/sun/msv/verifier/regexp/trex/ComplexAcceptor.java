/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.datatype.ValidationContextProvider;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;
import com.sun.tranquilo.verifier.Acceptor;
import com.sun.tranquilo.verifier.regexp.StringToken;
import com.sun.tranquilo.verifier.regexp.ElementToken;
import com.sun.tranquilo.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.tranquilo.verifier.regexp.ResidualCalculator;
import com.sun.tranquilo.util.StringRef;
import com.sun.tranquilo.util.DataTypeRef;

/**
 * Accept that is used when more than one pattern can be applicable to the current context.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final class ComplexAcceptor extends ComplexAcceptorBaseImpl
{
	protected final ElementExp[]	owners;
	
	public ComplexAcceptor(
		TREXDocumentDeclaration docDecl,
		Expression combined,
		Expression[] contentModels, ElementExp[] owners )
	{
		super( docDecl, combined, contentModels );
		this.owners = owners;
/*		
		int i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )	i++;
		
		contents = new Expression[i];
		owners = new ElementExp[i];
		
		i=0;
		for( CombinedChildContentExpCreator.OwnerAndContent o = primitives;
			 o!=null; o=o.next )
		{
			contents[i] = o.content;
			owners[i] = o.owner;
			i++;
		}
*/
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
