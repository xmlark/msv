package com.sun.tahiti.reader;

import com.sun.tahiti.grammar.AnnotatedGrammar;

public interface TahitiGrammarReader
{
	/**
	 * the same as the getResult method, but this one returns
	 * an AnnotatedGrammar object.
	 */
	AnnotatedGrammar getAnnotatedResult();
}
