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

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.verifier.regexp.AttributeFeeder;

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
