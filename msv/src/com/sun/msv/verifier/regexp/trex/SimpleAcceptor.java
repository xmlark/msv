package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.ElementToken;
import com.sun.tranquilo.verifier.regexp.ExpressionAcceptor;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;

public final class SimpleAcceptor extends ContentModelAcceptor
{
	/**
	 * the expression that should be used by the parent acceptor
	 * once if this acceptor is satisfied.
	 */
	protected final Expression continuation;
	
	public SimpleAcceptor(
		TREXDocumentDeclaration docDecl,
		Expression combined,
		Expression continuation )
	{
		super(docDecl,combined);
		if(continuation==null)		throw new Error();	// simple acceptor must have continuation
		this.continuation = continuation;
	}
}
