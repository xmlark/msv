package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;
import java.util.Iterator;
import java.util.Set;

public class Choice extends ContainerParticle
{
	protected Automaton getBaseAutomaton( AutomatonFactory factory )
	{
		// choice with many refs without any @occurs is quite common in many schema.
		// this implementation takes advantages of this tendency
		// to speed up automaton generation
		Set alphabets = new java.util.HashSet();

		Automaton a = factory.createNullAutomaton();
		
		final Iterator itr = children.iterator();
		while( itr.hasNext() )
		{
			final Particle p = (Particle)itr.next();
			if( p instanceof ElementRef )
			{
				final ElementRef er = (ElementRef)p;
				if( er.occurence == RepetableParticle.NORMAL )
				{
					// 'ref' elements without @occurs will be handled later.
					alphabets.add( er );
					continue;
				}
			}
			
			// other non-simple automata are simply unioned.
			a.destructiveUnion( p.getAutomaton(factory) );
		}
		
		factory.createMultipleSymbolsAutomaton( alphabets );
		
		return a;
	}
	
	protected Choice(Particle initialChild )
	{
		super( initialChild );
	}
	
	protected Choice() {}
}
