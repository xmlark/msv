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

import com.sun.msv.grammar.Expression;

/**
 * invokes State object that parses the document element.
 * 
 * This class is used to parse the first grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootState extends RootIncludedPatternState {
	
	public RootState() { super(null); }
	
	// GrammarState implements ExpressionState,
	// so RootState has to implement ExpressionOwner.
	public void onEndChild(Expression exp) {
		super.onEndChild(exp);
		
		final TREXBaseReader reader = (TREXBaseReader)this.reader;
		// perform final wrap-up.
		reader.wrapUp();
	}
}
