package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.AttributeFreeMarker;
import com.sun.tranquilo.grammar.trex.*;

class TREXAttributeFreeMarker
	extends AttributeFreeMarker
	implements TREXPatternVisitorBoolean
{
	private TREXAttributeFreeMarker() {}
	
	// singleton access
	public static final AttributeFreeMarker theInstance = new TREXAttributeFreeMarker();
	
	public boolean onInterleave( InterleavePattern exp )	{ return onBinExp(exp); }
	public boolean onConcur( ConcurPattern exp )			{ return onBinExp(exp); }
}