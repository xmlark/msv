package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.SequenceState;

public class MixedState extends SequenceState
{
	protected Expression annealExpression( Expression exp )
	{
		return reader.pool.createMixed(exp);
	}
}
