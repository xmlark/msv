package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.reader.State;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.TREXGrammar;

/**
 * parses &lt;grammar&gt; element.
 * 
 * this state is used to parse top-level grammars and nested grammars.
 * grammars merged by include element are handled by MergeGrammarState.
 * 
 * <p>
 * this class provides a new TREXGrammar object to localize names defined
 * within this grammar.
 */
class GrammarState extends MergeGrammarState
{
	protected TREXGrammar previousGrammar;
	protected TREXGrammar newGrammar;
	
	protected Expression makeExpression()
	{// start pattern is the grammar-as-a-pattern.
		return newGrammar.start;
	}

	protected void startSelf()
	{
		super.startSelf();
		
		previousGrammar = getReader().grammar;
		newGrammar = new TREXGrammar( getReader().getPool(), previousGrammar );
		getReader().grammar = newGrammar;
	}

	public void endSelf()
	{
		final TREXGrammar grammar = getReader().grammar;
		
		// detect references to undefined pattterns
		reader.detectUndefinedOnes(
			grammar.namedPatterns, TREXGrammarReader.ERR_UNDEFINED_PATTERN );

		// is start pattern defined?
		if( grammar.start==null )
		{
			reader.reportError( reader.ERR_MISSING_TOPLEVEL );
			grammar.start = Expression.nullSet;	// recover by assuming a valid pattern
		}
		
		// make sure that there is no recurisve patterns.
		grammar.start.visit( new TREXRunAwayExpressionChecker(getReader()) );
		if( !reader.hadError )
			// make sure that there is no sequenced string.
			// when run-away expression is found, calling this method results in
			// stack overflow.
			grammar.start.visit( new TREXSequencedStringChecker(getReader()) );

		// this method is called when this State is about to be removed.
		// restore the previous grammar
		if( previousGrammar!=null )
			getReader().grammar = previousGrammar;
		
		// if the previous grammar is null, it means this grammar is the top-level
		// grammar. In that case, leave it there so that GrammarReader can access
		// the loaded grammar.
			
		super.endSelf();
	}
}
