package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.ExpressionWithChildState;

public class InterleaveState extends ExpressionWithChildState
{
	protected Expression castExpression( Expression exp, Expression child )
	{
		// first one.
		if( exp==null )		return child;
		
		return ((TREXGrammarReader)reader).getPool().createInterleave(exp,child);
	}
}
