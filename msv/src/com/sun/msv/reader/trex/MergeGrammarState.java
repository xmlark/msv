/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.util.StartTagInfo;
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.reader.*;

/**
 * Used to parse merged grammar.
 * 
 * MergeGrammarState itself should not be a PatternState. However, GrammarState,
 * which is a derived class of this class, is a PatternState.
 * 
 * Therefore this class has to extend PatternState.
 */
public class MergeGrammarState extends ExpressionState implements ExpressionOwner
{
	protected final TREXGrammarReader getReader() { return (TREXGrammarReader)reader; }
	
	protected Expression makeExpression()
	{// this method doesn't provide any pattern
		return null;
	}

	protected State createChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("start"))	return new StartState();
		if(tag.localName.equals("define"))	return new DefineState();
		if(tag.localName.equals("include"))	return new IncludeMergeState();
		return null;
	}
	
	// DefineState and StartState is implemented by using ExpressionState.
	// By contract of that interface, this object has to implement ExpressionOwner.
	public void onEndChild( Expression exp ) {}	// do nothing.
}
