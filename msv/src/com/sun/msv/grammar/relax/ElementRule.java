package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.*;

/**
 * ElementRule declaration of RELAX.
 */
public class ElementRule extends ElementExp
{
	/** constraints over start tag of the element */
	public final TagClause clause;
	
	/** Attribute-free content model */
	public final Expression attributeFreeContentModel;
	
	protected ElementRules parent;

	/** gets the parent ElementRules object.
	 * 
	 * when this object is used as a named, no-inline elementRule,
	 * this variable holds a reference to the parent ElementRules object.
	 * otherwise, null
	 */
	public ElementRules getParent() { return parent; }
	
	public final NameClass getNameClass()	{ return clause.nameClass; }
	
	public ElementRule( ExpressionPool pool, TagClause clause, Expression contentModel )
	{
		super( pool.createSequence(clause,contentModel) );
		this.clause = clause;
		this.attributeFreeContentModel = contentModel;
	}
}
