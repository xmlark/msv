package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * &lt;empty /&gt; element
 */
public class Empty implements Particle
{
	public Automaton getAutomaton( AutomatonFactory factory )
	{
		return factory.createEmptyAutomaton();
	}
	
	// singleton access only
	private Empty() {}
	public static Empty theInstance = new Empty();
}
