package com.sun.tranquilo.reader.relax;

import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.util.StartTagInfo;

/**
 * Base implementation for HedgeRuleState and TopLevelState.
 * 
 * expects one and only one expression as its child.
 */
abstract class HedgeRuleBaseState extends SimpleState implements ExpressionOwner
{
	private Expression contentModel = null;
	
	public void onEndChild( Expression exp )
	{// this method is called after child expression is found and parsed
		if( contentModel!=null )
			reader.reportError( RELAXReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
			// recover by ignoring previous expression
		
		contentModel = exp;
	}
	
	protected final void endSelf()
	{
		super.endSelf();
		
		if( contentModel==null )
		{
			reader.reportError( RELAXReader.ERR_MISSING_CHILD_EXPRESSION );
			return;	// recover by ignoring this hedgeRule.
		}
		
		endSelf(contentModel);	// do something useful with child expression
	}
	
	/** derived class will receive child expression by this method */
	protected abstract void endSelf( Expression contentModel );

	
	protected State createChildState( StartTagInfo tag )
	{// particles only
		return reader.createExpressionChildState(tag);
	}
}
