package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.*;

public final class TREXPatternPool extends ExpressionPool
{
	public Expression createConcur( Expression left, Expression right )
	{
		if( left==Expression.nullSet || right==Expression.nullSet )	return Expression.nullSet;
		if( left==Expression.epsilon )
		{
			if( right.isEpsilonReducible() )	return Expression.epsilon;
			else								return Expression.nullSet;
		}
		if( right==Expression.epsilon )
		{
			if( left.isEpsilonReducible() )		return Expression.epsilon;
			else								return Expression.nullSet;
		}
		
		// associative operators are grouped to the right
		if( left instanceof ConcurPattern )
		{
			final ConcurPattern c = (ConcurPattern)left;
			
			return createConcur( c.exp1, createConcur(c.exp2, right) );
		}
		
		return unify(new ConcurPattern(left,right));
	}
	
	public Expression createInterleave( Expression left, Expression right )
	{
		if( left == Expression.epsilon )	return right;
		if( right== Expression.epsilon )	return left;
		if( left == Expression.nullSet
		||  right== Expression.nullSet )	return Expression.nullSet;
		
		// associative operators are grouped to the right
		if( left instanceof InterleavePattern )
		{
			final InterleavePattern i = (InterleavePattern)left;
			
			return createInterleave( i.exp1, createInterleave(i.exp2, right) );
		}
		
		return unify(new InterleavePattern(left,right));
	}
}
