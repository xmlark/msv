package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.reader.SequenceState;

public class StartState extends SequenceState
{
	protected final TREXGrammarReader getReader() { return (TREXGrammarReader)reader; }
	
	protected Expression annealExpression( Expression exp )
	{
		if(startTag.containsAttribute("name"))
		{// name attribute is optional.
			final String name = startTag.getAttribute("name");
			ReferenceExp ref = getReader().grammar.namedPatterns.getOrCreate(name);
			ref.exp = exp;
		}
		
		getReader().grammar.start = exp;
		return null;	// return value is meaningless.
	}
}
