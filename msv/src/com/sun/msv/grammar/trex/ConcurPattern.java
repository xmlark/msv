package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.*;

/**
 * &lt;concur&gt; pattern of TREX
 */
public final class ConcurPattern extends BinaryExp
{
	ConcurPattern( Expression left, Expression right ) { super(left,right,HASHCODE_CONCUR); }

	public Object visit( ExpressionVisitor visitor )
	{ return ((TREXPatternVisitor)visitor).onConcur(this); }

	public Expression visit( ExpressionVisitorExpression visitor )
	{ return ((TREXPatternVisitorExpression)visitor).onConcur(this); }
	
	public boolean visit( ExpressionVisitorBoolean visitor )
	{ return ((TREXPatternVisitorBoolean)visitor).onConcur(this); }

	public void visit( ExpressionVisitorVoid visitor )
	{ ((TREXPatternVisitorVoid)visitor).onConcur(this); }

	protected boolean calcEpsilonReducibility()
	{
		return exp1.isEpsilonReducible() && exp2.isEpsilonReducible();
	}
}
