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
import com.sun.msv.reader.State;

/**
 * parses the root state of a grammar included as a pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootIncludedPatternState extends RootState
{
	/**
	 * parsed external pattern will be reported to this object.
	 * 
	 * This state parses top-level, so parentState is null.
	 */
	private final IncludePatternState grandParent;
	
	protected RootIncludedPatternState( IncludePatternState grandpa )	{ this.grandParent = grandpa; }
		
	protected State createChildState( StartTagInfo tag )
	{// pattern elements are also allowed as document element in case of inclusion
		State next = reader.createExpressionChildState(this,tag);
		if(next!=null)	return next;
		
		return super.createChildState(tag);
	}

	public void onEndChild(Expression exp)
	{
		// this must be from grammar element. pass it to the IncludePatternState.
		grandParent.onEndChild(exp);
	}
}
