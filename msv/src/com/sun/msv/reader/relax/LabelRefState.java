package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.relax.RELAXModule;
import com.sun.tranquilo.reader.ExpressionWithoutChildState;
import com.sun.tranquilo.reader.GrammarReader;
														   
abstract class LabelRefState extends ExpressionWithoutChildState
{
	protected Expression makeExpression()
	{
		final String label = startTag.getAttribute("label");
		
		if(label==null)
		{// label attribute is required.
			reader.reportError( GrammarReader.ERR_MISSING_ATTRIBUTE,
				startTag.localName,"label");
			// recover by returning something that can be interpreted as Pattern
			return Expression.nullSet;
		}
		
		final RELAXReader reader = (RELAXReader)this.reader;
		final RELAXModule target = reader.resolveModuleReference(startTag);
		ReferenceExp ref = getOrCreate( target, label );
		
		// memorize this reference so that we can report the source of error
		// if this reference is an error.
		reader.backwardReference.memorizeLink(ref, target!=reader.currentModule );
		return ref;
	}
	
	/** gets or creates appropriate reference */
	protected abstract ReferenceExp getOrCreate( RELAXModule module, String label );
}
