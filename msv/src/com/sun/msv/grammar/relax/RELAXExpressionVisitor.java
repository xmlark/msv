package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.ExpressionVisitor;

public interface RELAXExpressionVisitor extends ExpressionVisitor
{
	// RELAX visitor can ignore onRef callback.
	Object onAttPool( AttPoolClause exp );
	Object onTag( TagClause exp );
	Object onElementRules( ElementRules exp );
	Object onHedgeRules( HedgeRules exp );
}
