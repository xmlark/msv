package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.ResidualCalculator;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.*;

class TREXResidualCalculator
	extends ResidualCalculator
	implements TREXPatternVisitorExpression
{
	TREXResidualCalculator( TREXPatternPool pool )	{ super(pool); }
	
	public Expression onConcur( ConcurPattern exp )
	{
		return ((TREXPatternPool)pool).createConcur(
			exp.exp1.visit(this), exp.exp2.visit(this) );
	}
	public Expression onInterleave( InterleavePattern exp )
	{
		TREXPatternPool pool = (TREXPatternPool)super.pool;
		return pool.createChoice(
			pool.createInterleave( exp.exp1.visit(this), exp.exp2 ),
			pool.createInterleave( exp.exp1, exp.exp2.visit(this) ) );
	}
}
