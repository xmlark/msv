package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

public class EmptyState extends ExpressionWithoutChildState
{
	protected Expression makeExpression() { return Expression.epsilon; }
}
