/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.*;

/**
 * ExpressionPool that can create TREX extension primitives.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXPatternPool extends ExpressionPool {
	
	public TREXPatternPool( ExpressionPool parent )	{ super(parent); }
	public TREXPatternPool()						{ super(); }
	
	public final Expression createConcur( Expression left, Expression right ) {
		if( left==Expression.nullSet || right==Expression.nullSet )	return Expression.nullSet;
		if( left==Expression.epsilon ) {
			if( right.isEpsilonReducible() )	return Expression.epsilon;
			else								return Expression.nullSet;
		}
		if( right==Expression.epsilon ) {
			if( left.isEpsilonReducible() )		return Expression.epsilon;
			else								return Expression.nullSet;
		}
		
		// associative operators are grouped to the right
		if( left instanceof ConcurPattern ) {
			final ConcurPattern c = (ConcurPattern)left;
			return createConcur( c.exp1, createConcur(c.exp2, right) );
		}
		
		return unify(new ConcurPattern(left,right));
	}
	
	public final Expression createInterleave( Expression left, Expression right ) {
		if( left == Expression.epsilon )	return right;
		if( right== Expression.epsilon )	return left;
		if( left == Expression.nullSet
		||  right== Expression.nullSet )	return Expression.nullSet;
		
		// associative operators are grouped to the right
		if( left instanceof InterleavePattern ) {
			final InterleavePattern i = (InterleavePattern)left;
			return createInterleave( i.exp1, createInterleave(i.exp2, right) );
		}
		
		return unify(new InterleavePattern(left,right));
	}
}
