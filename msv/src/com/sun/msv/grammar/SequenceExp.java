/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

/**
 * concatenation operator of the regular expression.
 */
public class SequenceExp extends BinaryExp
{
	SequenceExp( Expression left, Expression right )	{ super(left,right,HASHCODE_SEQUENCE); }
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onSequence(this); }
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onSequence(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onSequence(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onSequence(this); }

	protected boolean calcEpsilonReducibility()
	{
		return exp1.isEpsilonReducible() && exp2.isEpsilonReducible();
	}
}
