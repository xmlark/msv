package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * &lt;ref label='...'&gt; element
 */
public class ElementRef extends RepetableParticle
{
	/** ElementRules object that is referenced by this element */
	public ElementRules rule;
	
	protected Automaton getBaseAutomaton( AutomatonFactory factory )
	{
		return factory.createSingleSymbolAutomaton( rule );
	}
	
	public ElementRef( ElementRules rule )
	{
		this.rule = rule;
	}
}
