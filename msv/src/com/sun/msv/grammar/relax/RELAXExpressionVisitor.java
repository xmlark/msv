package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.ExpressionVisitor;

/**
 * Visitor interface for RELAX expressions.
 * 
 * By implementing this interface, your visitor can distinguish
 * four subclass of ReferenceExp introduced as RELAX stub.
 * 
 * <p>
 * Note that onRef method may still be called if you visit AGM created from
 * TREX pattern.
 */
public interface RELAXExpressionVisitor extends ExpressionVisitor
{
	Object onAttPool( AttPoolClause exp );
	Object onTag( TagClause exp );
	Object onElementRules( ElementRules exp );
	Object onHedgeRules( HedgeRules exp );
}
