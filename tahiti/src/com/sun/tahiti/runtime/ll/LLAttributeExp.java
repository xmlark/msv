package com.sun.tahiti.runtime.ll;

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.AttributeExp;

public class LLAttributeExp extends AttributeExp {
	/** LL parser table for this rule. */
	public LLParserTable parserTable;
	
	public LLAttributeExp( NameClass nc, Expression body ) {
		super(nc,body);
	}
}
