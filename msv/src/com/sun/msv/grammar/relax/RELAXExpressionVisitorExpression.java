package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ExpressionVisitorExpression;

public interface RELAXExpressionVisitorExpression extends ExpressionVisitorExpression
{
	// RELAX visitor can ignore onRef callback.
	Expression onAttPool( AttPoolClause exp );
	Expression onTag( TagClause exp );
	Expression onElementRules( ElementRules exp );
	Expression onHedgeRules( HedgeRules exp );
}
