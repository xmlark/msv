package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.State;

public class RootMergedGrammarState extends RootState
{
	protected State createChildState( StartTagInfo tag )
	{// expects "grammar" element only, and creates MergeGrammarState
		if( tag.localName.equals("grammar") )	return new MergeGrammarState();
		return null;
	}
}
