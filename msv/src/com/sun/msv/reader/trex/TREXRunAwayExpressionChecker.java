package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.reader.RunAwayExpressionChecker;
import com.sun.tranquilo.grammar.trex.TREXPatternVisitorVoid;
import com.sun.tranquilo.grammar.trex.ConcurPattern;
import com.sun.tranquilo.grammar.trex.InterleavePattern;
	
public class TREXRunAwayExpressionChecker
	extends RunAwayExpressionChecker
	implements TREXPatternVisitorVoid
{
	public TREXRunAwayExpressionChecker( TREXGrammarReader reader ) { super(reader); }

	public void onConcur( ConcurPattern exp )			{ binaryVisit(exp);	}
	public void onInterleave( InterleavePattern exp )	{ binaryVisit(exp);	}
}
