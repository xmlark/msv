package com.sun.tahiti.runtime.ll;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.ElementPattern;

public class LLElementExp extends ElementPattern {
	/** LL parser table for this rule. */
	public LLParserTable parserTable;
	
	public LLElementExp( NameClass nc ) {
		this(nc,Expression.nullSet);
	}

	public LLElementExp( NameClass nc, Expression body ) {
		super(nc,body);
	}
}
