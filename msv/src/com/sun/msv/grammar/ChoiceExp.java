package com.sun.tranquilo.grammar;

/**
 * Union operator
 */
public final class ChoiceExp extends BinaryExp
{
	ChoiceExp( Expression left, Expression right )
	{
		super(left,right,HASHCODE_CHOICE);
	}
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onChoice(this); }
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onChoice(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onChoice(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onChoice(this); }

	protected boolean calcEpsilonReducibility()
	{
		return exp1.isEpsilonReducible() || exp2.isEpsilonReducible();
	}
}
