package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.ExpressionVisitorBoolean;

/**
 * TREX version of ExpressionVisitorBoolean
 */
public interface TREXPatternVisitorBoolean extends ExpressionVisitorBoolean
{
	boolean onConcur( ConcurPattern p );
	boolean onInterleave( InterleavePattern p );
}
