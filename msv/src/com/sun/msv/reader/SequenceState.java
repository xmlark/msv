package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

public class SequenceState extends ExpressionWithChildState
{
	protected Expression castExpression( Expression exp, Expression child )
	{
		// first one.
		if( exp==null )	return child;
		return reader.pool.createSequence(exp,child);
	}
}
