package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.reader.SimpleState;
import com.sun.tranquilo.reader.ExpressionOwner;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.TREXGrammar;

/**
 * invokes State object that parses the document element.
 * 
 * This class accepts grammar element only.
 */
class RootState extends SimpleState implements ExpressionOwner
{
	protected State createChildState( StartTagInfo tag )
	{
		// grammar has to be treated separately so as not to
		// create unnecessary TREXGrammar object.
		if(tag.localName.equals("grammar"))
			return new GrammarState();
		
		State s = reader.createExpressionChildState(tag);
		if(s!=null)
		{// other pattern element is specified.
			// create wrapper grammar
			final TREXGrammarReader reader = (TREXGrammarReader)this.reader;
			reader.grammar = new TREXGrammar( reader.getPool(), null );
			simple = true;
		}
		
		return s;
	}
	
	/**
	 * a flag that indicates 'grammar' element was not used.
	 * In that case, this object is responsible to set start pattern.
	 */
	private boolean simple = false;
	
	// GrammarState implements ExpressionState,
	// so RootState has to implement ExpressionOwner.
	public void onEndChild(Expression exp)
	{
		if( simple )	((TREXGrammarReader)reader).grammar.start = exp;
	}
}
