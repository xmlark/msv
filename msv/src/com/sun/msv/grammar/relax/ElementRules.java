package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.*;

/**
 * Set of ElementRule objects that share the label name.
 * 
 * ReferenceExp.exp contains choice of ElementRule objects.
 */
public class ElementRules extends ReferenceExp implements Exportable
{
	protected ElementRules( String label, RELAXModule ownerModule )
	{
		super(label);
		this.ownerModule = ownerModule;
	}
	
	public boolean equals( Object o )
	{
		return this==o;
	}
	
	protected boolean calcEpsilonReducibility()
	{// elementRules are always not epsilon-reducible.
		return false;
	}
	
	public void addElementRule( ExpressionPool pool, ElementRule newRule )
	{
		if( exp==null )		// the first element
			exp = newRule;
		else
			exp = pool.createChoice(exp,newRule);
	}

	public Object visit( RELAXExpressionVisitor visitor )
	{ return visitor.onElementRules(this); }

	public Expression visit( RELAXExpressionVisitorExpression visitor )
	{ return visitor.onElementRules(this); }
	
	public boolean visit( RELAXExpressionVisitorBoolean visitor )
	{ return visitor.onElementRules(this); }

	public void visit( RELAXExpressionVisitorVoid visitor )
	{ visitor.onElementRules(this); }

	/**
	 * a flag that indicates this elementRule is exported and
	 * therefore accessible from other modules.
	 */
	public boolean exported = false;
	public boolean isExported() { return exported; }
	
	/** RELAXModule object to which this object belongs */
	public final RELAXModule ownerModule;
}