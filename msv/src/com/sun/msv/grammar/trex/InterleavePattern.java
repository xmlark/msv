package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.*;

/**
 * &lt;interleave&gt; pattern of TREX.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class InterleavePattern extends BinaryExp {
	InterleavePattern( Expression left, Expression right ) {
		super(left,right,HASHCODE_INTERLEAVE);
	}
	public Object visit( ExpressionVisitor visitor ) {
		return ((TREXPatternVisitor)visitor).onInterleave(this);
	}
	public Expression visit( ExpressionVisitorExpression visitor ) {
		return ((TREXPatternVisitorExpression)visitor).onInterleave(this);
	}
	public boolean visit( ExpressionVisitorBoolean visitor ) {
		return ((TREXPatternVisitorBoolean)visitor).onInterleave(this);
	}
	public void visit( ExpressionVisitorVoid visitor ) {
		((TREXPatternVisitorVoid)visitor).onInterleave(this);
	}
	protected boolean calcEpsilonReducibility() {
		return exp1.isEpsilonReducible() && exp2.isEpsilonReducible();
	}
}
