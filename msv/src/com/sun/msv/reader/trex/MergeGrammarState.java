/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.util.StartTagInfo;
import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.*;

/**
 * Used to parse merged grammar.
 * 
 * MergeGrammarState itself should not be a PatternState. However, GrammarState,
 * which is a derived class of this class, is a PatternState.
 * 
 * Therefore this class has to extend PatternState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class MergeGrammarState extends ExpressionState implements ExpressionOwner
{
	protected final TREXBaseReader getReader() { return (TREXBaseReader)reader; }
	
	protected Expression makeExpression()
	{// this method doesn't provide any pattern
		return null;
	}

	protected State createChildState( StartTagInfo tag )
	{
		if(tag.localName.equals("start"))	return getReader().sfactory.start(this,tag);
		if(tag.localName.equals("define"))	return getReader().sfactory.define(this,tag);
		if(tag.localName.equals("include"))	return getReader().sfactory.includeGrammar(this,tag);
		return null;
	}
	
	// DefineState and StartState is implemented by using ExpressionState.
	// By contract of that interface, this object has to implement ExpressionOwner.
	public void onEndChild( Expression exp ) {}	// do nothing.
}
