package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;

abstract class ClauseState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag )
	{
		if(!tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace))	return null;
		
		if(tag.localName.equals("ref"))			return new AttPoolRefState();
		if(tag.localName.equals("attribute"))	return new AttributeState();
		
		return null;	// unrecognized
	}
	
	protected Expression initialExpression()	{ return Expression.epsilon; }
	
	protected Expression castExpression( Expression exp, Expression child )
	{// attributes and references are combined in one sequence
		return reader.pool.createSequence(exp,child);
	}
	
	// TODO: check multiple constraint over the same attribute

	/** gets reader in type-safe fashion */
	protected RELAXReader getReader() { return (RELAXReader)reader; }



	/**
	 * expression object that is being created.
	 * See {@link castPattern} and {@link annealPattern} methods
	 * for how will a pattern be created.
	 */
	protected Expression exp = initialExpression();
	
	/** receives a Pattern object that is contained in this element. */
	public final void onEndChild( Expression childExpression )
	{
		exp = castExpression( exp, childExpression );
	}
}
