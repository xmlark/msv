package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.ExpressionVisitor;

/**
 * Visitor interface for TREX pattern
 * 
 * Note that traversing pattern is still a job for implementator.
 * Pattern and derived types do not provide any traversal.
 */
public interface TREXPatternVisitor extends ExpressionVisitor
{
	Object onConcur( ConcurPattern p );
	Object onInterleave( InterleavePattern p );
}
