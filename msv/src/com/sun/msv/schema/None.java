package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * &lt;none /&gt; element
 */
public class None implements Particle
{
	// singleton access only
	private None() {}
	public static None theInstance = new None();
	
	public Automaton getAutomaton( AutomatonFactory factory )
	{
		return factory.createNullAutomaton();
	}
}
