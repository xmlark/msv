package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.ElementToken;
import com.sun.tranquilo.verifier.regexp.ExpressionAcceptor;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;

/**
 * Acceptor that will be used when only one ElementExp matches
 * the start tag.
 */
public final class SimpleAcceptor extends ContentModelAcceptor
{
	/**
	 * the expression that should be used by the parent acceptor
	 * once if this acceptor is satisfied.
	 */
	protected final Expression continuation;
	
	/**
	 * ElementExp that accepted the start tag.
	 * 
	 * This acceptor is verifying the content model of this ElementExp.
	 * This value is usually non-null, but can be null when Verifier is
	 * recovering from eariler errors.
	 */
	protected final ElementExp owner;

	public final Object getOwnerType()	{ return owner; }

	public SimpleAcceptor(
		TREXDocumentDeclaration docDecl,
		Expression combined,
		ElementExp owner,
		Expression continuation )
	{
		super(docDecl,combined);
		if(continuation==null)		throw new Error();	// simple acceptor must have continuation
		this.continuation	= continuation;
		this.owner			= owner;
	}
}
