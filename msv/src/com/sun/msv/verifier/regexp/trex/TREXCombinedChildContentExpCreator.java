package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.tranquilo.verifier.regexp.StartTagInfoEx;

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
		return new ExpressionPair(
			((TREXPatternPool)pool).createConcur(
			((ExpressionPair)exp.exp1.visit(this)).content,
			((ExpressionPair)exp.exp2.visit(this)).content),
			Expression.nullSet );
		// for concur to be a successful combined child content expression,
		// more than two ElementExps must match the start tag.
		// In that case, continuation will never be used.
		// If concur fails, then we should return nullSet.
		
		// so returning nullSet may looks strange, but it is correct.
	}
	public Object onInterleave( InterleavePattern exp )
	{
		ExpressionPair p1 = (ExpressionPair)exp.exp1.visit(this);
		ExpressionPair p2 = (ExpressionPair)exp.exp2.visit(this);
		
		if(p2.content==Expression.nullSet)
			return new ExpressionPair( p1.content,
				((TREXPatternPool)pool).createInterleave(p1.continuation,exp.exp2) );
		
		if(p1.content==Expression.nullSet)
			return new ExpressionPair( p2.content,
				((TREXPatternPool)pool).createInterleave(p2.continuation,exp.exp1) );
		
		// in this case, continuation will never be used because
		// it has more than one element of concern.
		return new ExpressionPair(
			pool.createChoice( p1.content, p2.content ),
			Expression.nullSet );
	}
}
