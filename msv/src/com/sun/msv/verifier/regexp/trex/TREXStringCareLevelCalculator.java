/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp.trex;

import com.sun.tranquilo.verifier.regexp.StringCareLevelCalculator;
import com.sun.tranquilo.grammar.trex.*;

/**
 * TREX-extended StringCareLevelCalculator.
 * 
 * this class is also thread safe.
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
