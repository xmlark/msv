package com.sun.tranquilo.grammar;

/**
 * '+' operator of the regular expression.
 */
public final class OneOrMoreExp extends UnaryExp
{
	OneOrMoreExp( Expression exp )	{ super( exp,HASHCODE_ONE_OR_MORE ); }
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onOneOrMore(this);	}
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onOneOrMore(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onOneOrMore(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onOneOrMore(this); }

	protected boolean calcEpsilonReducibility()
	{
		return exp.isEpsilonReducible();
	}
}
