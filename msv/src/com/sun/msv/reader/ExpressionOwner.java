package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

/**
 * interface that must be implemented by the parent state of ExpressionState.
 * 
 * ExpressionState notifies its parent by using this interface.
 */
public interface ExpressionOwner
{
	void onEndChild( Expression exp );
}
