package com.sun.tahiti.runtime.ll;

public interface LLParserTable {
	/**
	 * looks up LL parsing table.
	 * 
	 * @param	symStackTop
	 *		the symbol of the current stack top.
	 * @param	symInput
	 *		the symbol of the current input token.
	 * @return
	 *		null if there is no rule to apply.
	 */
	Rule[] get( Object symStackTop, Object symInput );
}
