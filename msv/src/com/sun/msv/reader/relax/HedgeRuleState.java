package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.relax.HedgeRules;

/**
 * &lt;hedgeRule&gt; element
 * 
 * this class is used as the base class of TopLevelState
 */
public class HedgeRuleState extends HedgeRuleBaseState
{
	protected void endSelf( Expression contentModel )
	{
		final String label = startTag.getAttribute("label");
		if( label==null )
		{
			reader.reportError( RELAXReader.ERR_MISSING_ATTRIBUTE, "hedgeRule", "label" );
			return;	// recover by ignoring this hedgeRule
		}
		
		final RELAXReader reader = (RELAXReader)this.reader;
		
		HedgeRules hr = reader.currentModule.hedgeRules.getOrCreate(label);
		reader.setDeclaredLocationOf(hr); // remember where this hedgeRule is defined.
		hr.addHedge(contentModel,reader.pool);
	}
}
