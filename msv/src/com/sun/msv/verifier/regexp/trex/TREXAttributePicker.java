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

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.verifier.regexp.AttributePicker;

/**
 * TREX-extended AttributePicker
 * 
 * removes all unnecessary expressions and
 * creates an expression that consists of required attributes and choices only.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXAttributePicker extends AttributePicker
	implements TREXPatternVisitorExpression {

	TREXAttributePicker( TREXPatternPool pool ) { super(pool); }
	
	public Expression onInterleave( InterleavePattern exp ) {
		Expression ex1 = exp.exp1.visit(this);
		Expression ex2 = exp.exp2.visit(this);
		
		if(ex1.isEpsilonReducible()) {
			if(ex2.isEpsilonReducible())	return Expression.epsilon;
			else							return ex2;
		} else
			return ex1;
	}
	
	public Expression onConcur( ConcurPattern exp ) {
		// abandon concur.
		return Expression.epsilon;
	}

}
