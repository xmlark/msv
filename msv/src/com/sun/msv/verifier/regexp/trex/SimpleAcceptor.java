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

import com.sun.tranquilo.verifier.regexp.ElementToken;
import com.sun.tranquilo.verifier.regexp.ExpressionAcceptor;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ElementExp;

/**
 * Acceptor that will be used when only one ElementExp matches
 * the start tag.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
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
	 * null owner means this acceptor is "synthesized" just for proper error recovery,
	 * therefor there is no owner element expression.
	 */
	public final ElementExp owner;

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
