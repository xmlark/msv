package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.ExpressionVisitorBoolean;

public interface RELAXExpressionVisitorBoolean extends ExpressionVisitorBoolean
{
	// RELAX visitor can ignore onRef callback.
	boolean onAttPool( AttPoolClause exp );
	boolean onTag( TagClause exp );
	boolean onElementRules( ElementRules exp );
	boolean onHedgeRules( HedgeRules exp );
}
