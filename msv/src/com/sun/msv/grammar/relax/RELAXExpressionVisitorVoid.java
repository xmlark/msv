package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.ExpressionVisitorVoid;

public interface RELAXExpressionVisitorVoid extends ExpressionVisitorVoid
{
	// RELAX visitor can ignore onRef callback.
	void onAttPool( AttPoolClause exp );
	void onTag( TagClause exp );
	void onElementRules( ElementRules exp );
	void onHedgeRules( HedgeRules exp );
}
