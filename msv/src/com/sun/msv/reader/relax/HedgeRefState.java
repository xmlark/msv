package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.relax.RELAXModule;
														   
public class HedgeRefState extends LabelRefState
{
	protected final ReferenceExp getOrCreate( RELAXModule module, String label )
	{
		return module.hedgeRules.getOrCreate(label);
	}
}
