package com.sun.tahiti.runtime.ll;


public interface BindableGrammar extends com.sun.msv.grammar.Grammar {
	LLParserTable getRootTable();
	Object getRootSymbol();
}
