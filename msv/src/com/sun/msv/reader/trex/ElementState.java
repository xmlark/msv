package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.ElementPattern;

public class ElementState extends NameClassAndExpressionState
{
	protected Expression annealExpression( Expression contentModel )
	{
		return new ElementPattern( nameClass, contentModel );
	}
}
