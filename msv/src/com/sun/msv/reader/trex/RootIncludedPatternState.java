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
import com.sun.tranquilo.reader.State;

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
		State next = reader.createExpressionChildState(tag);
		if(next!=null)	return next;
		
		return super.createChildState(tag);
	}

	public void onEndChild(Expression exp)
	{
		// this must be from grammar element. pass it to the IncludePatternState.
		grandParent.onEndChild(exp);
	}
}
