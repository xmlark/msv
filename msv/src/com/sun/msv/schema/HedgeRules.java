package com.sun.tranquilo.schema;

import java.util.Set;
import java.util.Iterator;
import jp.gr.xml.formal.automaton.Automaton;
import jp.gr.xml.formal.automaton.AutomatonFactory;

// TODO : should this be derived from Model, Particle, or Rule?
public class HedgeRules implements Exportable, Particle
{
	/** actual storage that keeps HedgeRule objects */
	private final Set impl = new java.util.HashSet();
	
	/** adds new HedgeRule */
	protected void add( HedgeRuleX rule )
	{
		// ASSERT : rule.label==label
		impl.add(rule);
	}
	
	/** iterates all HedgeRule object in this object */
	public Iterator iterator()				{ return impl.iterator(); }
	
	protected HedgeRules() {}
	
	/** this flag indicates that whether the specified label name is exported */
	protected boolean exported;
	/** examines whether this label is exported or not */
	public boolean isExported() { return exported; }
	
	/** returns an union of all HedgeRule objects in it  */
	public Automaton getAutomaton( AutomatonFactory factory )
	{
		final Iterator itr = iterator();
		Automaton a = factory.createNullAutomaton();
		while(itr.hasNext())
		{
			final HedgeRuleX hr = (HedgeRuleX)itr.next();
			a.destructiveUnion( hr.getAutomaton(factory) );
		}
		return a;
	}
}
