package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.ExpressionVisitorVoid;

/**
 * TREX version of ExpressionVisitorVoid
 */
public interface TREXPatternVisitorVoid extends ExpressionVisitorVoid
{
	void onConcur( ConcurPattern p );
	void onInterleave( InterleavePattern p );
}
