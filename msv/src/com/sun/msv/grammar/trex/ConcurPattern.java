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
 * &lt;concur&gt; pattern of TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ConcurPattern extends BinaryExp {
	
	ConcurPattern( Expression left, Expression right ) { super(left,right,HASHCODE_CONCUR); }

	public Object visit( ExpressionVisitor visitor ) {
		return ((TREXPatternVisitor)visitor).onConcur(this);
	}

	public Expression visit( ExpressionVisitorExpression visitor ) {
		return ((TREXPatternVisitorExpression)visitor).onConcur(this);
	}
	
	public boolean visit( ExpressionVisitorBoolean visitor ) {
		return ((TREXPatternVisitorBoolean)visitor).onConcur(this);
	}

	public void visit( ExpressionVisitorVoid visitor ) {
		((TREXPatternVisitorVoid)visitor).onConcur(this);
	}

	protected boolean calcEpsilonReducibility() {
		return exp1.isEpsilonReducible() && exp2.isEpsilonReducible();
	}
}
