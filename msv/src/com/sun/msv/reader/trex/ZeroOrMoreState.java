package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.SequenceState;

class ZeroOrMoreState extends SequenceState
{
	protected Expression annealExpression( Expression exp )
	{
		return reader.pool.createZeroOrMore(exp);
	}
}
