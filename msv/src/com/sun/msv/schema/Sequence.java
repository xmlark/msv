package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;
import java.util.Iterator;

public class Sequence extends ContainerParticle
{
	protected Automaton getBaseAutomaton( AutomatonFactory factory )
	{
		// TODO : this method may be better implemented.
		Automaton a = factory.createEmptyAutomaton();
		
		final Iterator itr = children.iterator();
		while( itr.hasNext() )
		{
			final Particle p = (Particle)itr.next();
			a.destructiveConcatenation( p.getAutomaton(factory) );
		}
		
		return a;
	}
}
