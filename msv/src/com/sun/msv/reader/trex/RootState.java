package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;

/**
 * invokes State object that parses the document element.
 * 
 * This class accepts grammar element only.
 */
class RootState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("grammar"))
			return new GrammarState();
		return null;
	}
	
	// GrammarState implements ExpressionState,
	// so RootState has to implement ExpressionOwner.
	public void onEndChild(Expression exp) {}
}
