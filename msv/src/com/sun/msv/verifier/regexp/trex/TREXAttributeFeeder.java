/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.verifier.regexp.AttributeFeeder;

/**
 * {@link AttributeFeeder} which can handle TREX extension primitives.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final class TREXAttributeFeeder
	extends AttributeFeeder
	implements TREXPatternVisitorExpression
{
	TREXAttributeFeeder( TREXDocumentDeclaration docDecl )	{ super( docDecl); }

	public Expression onConcur( ConcurPattern exp )
	{
		return ((TREXPatternPool)pool).createConcur( exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	public Expression onInterleave( InterleavePattern exp )
	{
		return pool.createChoice(
			((TREXPatternPool)pool).createInterleave( exp.exp1.visit(this), exp.exp2 ),
			((TREXPatternPool)pool).createInterleave( exp.exp1, exp.exp2.visit(this) ) );
	}
}
