/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp.trex;

import com.sun.msv.verifier.regexp.StringCareLevelCalculator;
import com.sun.msv.grammar.trex.*;

/**
 * {@link StringCareLevelCalculator} which can handle TREX extension primitives.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TREXStringCareLevelCalculator
	extends StringCareLevelCalculator
	implements TREXPatternVisitorBoolean
{
	private TREXStringCareLevelCalculator() {}
	
	// singleton access
	public static final StringCareLevelCalculator theInstance = new TREXStringCareLevelCalculator();
	
	public boolean onInterleave( InterleavePattern exp )
	{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
	
	public boolean onConcur( ConcurPattern exp )		
	{ return exp.exp1.visit(this)||exp.exp2.visit(this); }
}
