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
 * clones an expression.
 * 
 * This class is used as a default implementation for relevant task.
 * Theere are no default implementations for onAttribute, onElement, and onRef methods.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class TREXPatternCloner extends ExpressionCloner
	implements TREXPatternVisitorExpression {
	
	public TREXPatternCloner( ExpressionPool pool ) {
		// TREXPatternCloner can be used with normal ExpressionPool
		// when the expression doesn't contain any TREX primitives.
		// So this constructor receives ExpressionPool instead of
		// TREXPatternPool.
		super(pool);
	}
	
	public Expression onConcur( ConcurPattern p ) {
		return ((TREXPatternPool)pool).createConcur(
			p.exp1.visit(this),p.exp2.visit(this));
	}
	public Expression onInterleave( InterleavePattern p ) {
		return ((TREXPatternPool)pool).createInterleave(
			p.exp1.visit(this),p.exp2.visit(this));
	}
}