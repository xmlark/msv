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

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.msv.verifier.regexp.StartTagInfoEx;

/**
 * {@link CombinedChildContentExpCreator} which can handle TREX extension primitives.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TREXCombinedChildContentExpCreator
	extends CombinedChildContentExpCreator
	implements TREXPatternVisitor
{
	/**
	 * a flag that indicates that we have 'concur' element to combine
	 * elements of concern.
	 * 
	 * If 'concur' is used, we have to keep track of combined child content
	 * expression to detect errors. If 'concur' is not used, then
	 * keeping track of all primitive child content expressions are enough
	 * to detect errors.
	 */
	private boolean foundConcur;
	
	TREXCombinedChildContentExpCreator(
		TREXPatternPool pool, TREXAttributeFeeder feeder )
	{
		super(pool,feeder);
	}

	public ExpressionPair get( Expression combinedPattern, StartTagInfoEx info, boolean feedAttributes, boolean checkTagName )
	{
		foundConcur = false;
		return super.get( combinedPattern, info, feedAttributes, checkTagName );
	}
	
	public final boolean isComplex() { return foundConcur; }

	public Object onConcur( ConcurPattern exp )
	{
		foundConcur = true;
		TREXPatternPool pool = (TREXPatternPool)this.pool;
		ExpressionPair p1 = (ExpressionPair)exp.exp1.visit(this);
		ExpressionPair p2 = (ExpressionPair)exp.exp2.visit(this);
		
		return new ExpressionPair(
			pool.createConcur(p1.content,p2.content),
			pool.createConcur(p1.continuation,p2.continuation) );
	}
	public Object onInterleave( InterleavePattern exp )
	{
		TREXPatternPool pool = (TREXPatternPool)this.pool;
		ExpressionPair p1 = (ExpressionPair)exp.exp1.visit(this);
		ExpressionPair p2 = (ExpressionPair)exp.exp2.visit(this);
		
		if(p2.content==Expression.nullSet)
			return new ExpressionPair( p1.content,
				pool.createInterleave(p1.continuation,exp.exp2) );
		
		if(p1.content==Expression.nullSet)
			return new ExpressionPair( p2.content,
				pool.createInterleave(p2.continuation,exp.exp1) );

		// now the situation is (A,X)^(A,Y).
		// so the continuation after eating A will be X^Y.
		return new ExpressionPair(
			pool.createChoice( p1.content, p2.content ),
			pool.createInterleave( p1.continuation, p2.continuation ) );
	}
}
