package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.*;

/**
 * 'tag'  of RELAX module.
 * 
 * ReferenceExp.exp contains a sequence of AttributeExp.
 */
public class TagClause extends ReferenceExp
{
	/** tag name constraint.
	 * 
	 * This should be SimpleNameClass. The only exception is for stub module.
	 */
	public NameClass nameClass;
	
	/** RefContainer-controlled creation. should be created via RefContainer.getOrCreate */
	protected TagClause( String role )	{ super(role); }
	
	/** constructor for inline tag. creatable directly from outside */
	public TagClause() { super(null); }
	
	public Object visit( RELAXExpressionVisitor visitor )
	{ return visitor.onTag(this); }

	public Expression visit( RELAXExpressionVisitorExpression visitor )
	{ return visitor.onTag(this); }
	
	public boolean visit( RELAXExpressionVisitorBoolean visitor )
	{ return visitor.onTag(this); }

	public void visit( RELAXExpressionVisitorVoid visitor )
	{ visitor.onTag(this); }

}