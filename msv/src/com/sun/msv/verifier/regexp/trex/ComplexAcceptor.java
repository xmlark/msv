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
final class ComplexAcceptor extends ContentModelAcceptor
{
	protected final Expression[]	contents;
	protected final ElementExp[]	owners;
	
	public ComplexAcceptor(
		TREXDocumentDeclaration docDecl,
		Expression combined,
		CombinedChildContentExpCreator.OwnerAndContent primitives )
	{
		super( docDecl, combined );
		
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
	}

	
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

	/** eats string literal */
	public boolean stepForward( String literal, ValidationContextProvider context, StringRef refErr, DataTypeRef refType )
	{
		if(!super.stepForward(literal,context,refErr,refType))	return false;

		final StringToken token = new StringToken(literal,context);
		final ResidualCalculator res = docDecl.getResidualCalculator();

		// some may become invalid, but at least one always remain valid
		for( int i=0; i<contents.length; i++ )
			contents[i] = res.calcResidual( contents[i], token );
		
		return true;
	}
	
	public boolean stepForward( Acceptor child, StringRef errRef )
	{
		if(!super.stepForward(child,errRef))	return false;

		final ResidualCalculator res = docDecl.getResidualCalculator();
		ElementToken token;
		
		if( child instanceof SimpleAcceptor )
			// this is possible although it is very rare.
			// continuation cannot be used here, because
			// some contents[i] may reject this owner.
			token = new ElementToken( new ElementExp[]{((SimpleAcceptor)child).owner} );
		else
			if( errRef!=null )
				// in error recovery mode
				// pretend that every candidate of child ComplexAcceptor is happy
				token = new ElementToken( ((ComplexAcceptor)child).owners );
			else
				// in normal mode, collect only those satisfied owners.
				token = new ElementToken( getSatisfiedOwners() );
		
		for( int i=0; i<contents.length; i++ )
			contents[i] = docDecl.getResidualCalculator().calcResidual( contents[i], token );
		
		return true;
	}
}
