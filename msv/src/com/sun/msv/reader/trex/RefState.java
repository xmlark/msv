package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.reader.ExpressionWithoutChildState;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.trex.TREXGrammar;
														   
public class RefState extends ExpressionWithoutChildState
{
	protected Expression makeExpression()
	{
		if(!startTag.containsAttribute("name"))
		{// name attribute is required.
			reader.reportError( TREXGrammarReader.ERR_MISSING_ATTRIBUTE,
				"ref","name");
			// recover by returning something that can be interpreted as Pattern
			return Expression.nullSet;
		}
		
		final String name = startTag.getAttribute("name");
		TREXGrammar grammar = ((TREXGrammarReader)this.reader).grammar;
		
		if(startTag.containsAttribute("parent")
		&& startTag.getAttribute("parent").equals("true") )
			grammar = grammar.getParentGrammar();
		
		ReferenceExp r = grammar.namedPatterns.getOrCreate(name);
		reader.backwardReference.memorizeLink(r,false);
		return r;
	}
}
