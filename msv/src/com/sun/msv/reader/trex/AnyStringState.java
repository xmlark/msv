package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.ExpressionWithoutChildState;

public class AnyStringState extends ExpressionWithoutChildState
{
	protected Expression makeExpression() { return Expression.anyString; }
}
