package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.SequenceState;

class OneOrMoreState extends SequenceState
{
	protected Expression annealExpression( Expression exp )
	{
		return reader.pool.createOneOrMore(exp);
	}
}
