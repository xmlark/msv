package com.sun.tranquilo.reader.trex.typed;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.trex.ElementState;
import com.sun.tranquilo.grammar.trex.typed.TypedElementPattern;

/**
 * reads &lt;element&gt; element with 'label' annotation.
 */
public class TypedElementState extends ElementState
{
	protected Expression annealExpression( Expression contentModel )
	{
		final String label = startTag.getAttribute( TypedTREXGrammarReader.LABEL_NAMESPACE, "label" );
		if( label==null )
			return super.annealExpression( contentModel );
		else
			return new TypedElementPattern( nameClass, contentModel, label );
	}
}
