package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

public class NullSetState extends ExpressionWithoutChildState
{
	protected Expression makeExpression() { return Expression.nullSet; }
}
