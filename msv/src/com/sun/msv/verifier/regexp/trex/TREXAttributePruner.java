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

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.verifier.regexp.AttributePruner;

/**
 * Creates a pattern whose AttributePattern is completely replaced by nullSet.
 * 
 * This step is used to remove all unconsumed AttributePattern from the pattern.
 */
final class TREXAttributePruner
	extends AttributePruner
	implements TREXPatternVisitorExpression
{
	TREXAttributePruner( TREXPatternPool pool ) { super(pool); }
	
	public Expression onInterleave( InterleavePattern exp )
	{
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return ((TREXPatternPool)pool).createInterleave(np1,np2);
	}
	public Expression onConcur( ConcurPattern exp )
	{
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return ((TREXPatternPool)pool).createConcur(np1,np2);
	}
}
