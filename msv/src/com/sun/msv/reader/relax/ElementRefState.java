package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.relax.RELAXModule;
														   
public class ElementRefState extends LabelRefState
{
	protected final ReferenceExp getOrCreate( RELAXModule module, String label )
	{
		return module.elementRules.getOrCreate(label);
	}
}
