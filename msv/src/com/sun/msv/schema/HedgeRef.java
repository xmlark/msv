package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * &lt;ref hedgeLabel='...'&gt; element
 */
public class HedgeRef extends RepetableParticle
{
	/** HedgeRule object that is referenced by this element */
	public HedgeRules hedgeRules;
	
	protected Automaton getBaseAutomaton( AutomatonFactory factory )
	{
		return hedgeRules.getAutomaton(factory);
	}
	
	public HedgeRef( HedgeRules rule )
	{
		this.hedgeRules = rule;
	}
}
