package com.sun.tranquilo.relaxns.grammar;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;

public class RuleImpl implements org.iso_relax.dispatcher.Rule
{
	/** "meat" of this Rule. */
	public final Expression exp;
	
	/** name of this rule */
	protected final String name;
	
	public RuleImpl( ReferenceExp exp ) { this(exp.name,exp.exp); }
	public RuleImpl( String name, Expression exp )
	{
		this.exp=exp;
		this.name=name;
	}
	
	public String getName() { return name; }
}
