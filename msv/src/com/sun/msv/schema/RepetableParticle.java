package com.sun.tranquilo.schema;

import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

/**
 * particle with 'occurs' attribute
 */
public abstract class RepetableParticle implements Particle
{
	// constants of occurence
	
	/** not qualified with 'occurs' attribute */
	public static final int NORMAL		= 0;
	/** optional. The particle can be omitted. Denoted by A? */
	public static final int OPTIONAL	= 1;
	/** repeatable. Denoted by A+ */
	public static final int PLUS		= 2;
	/** optional and repeatable. Denoted by A* */
	public static final int STAR		= 3;
	
	/** occurence of this particle. NORMAL, OPTIONAL, PLUS or STAR */
	public int occurence;
	
	
	public final Automaton getAutomaton( AutomatonFactory factory )
	{
		Automaton a = getBaseAutomaton(factory);
		switch( occurence )
		{
		case NORMAL:	return a;
		case OPTIONAL:	return factory.question(a);
		case PLUS:		return factory.plus(a);
		case STAR:		return factory.star(a);
		// assertion failed: occurence should be NORMAL, OPTIONAL, PLUS, or STAR
		default:		throw new IllegalStateException();
		}
	}
	
	/** derived class should implement this method, instead of getAutomaton */
	protected abstract Automaton getBaseAutomaton( AutomatonFactory factory );
}
