/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * clones an expression.
 * 
 * This class is used as a default implementation for relevant task.
 * Theere are no default implementations for onAttribute, onElement, and onRef methods.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionCloner implements ExpressionVisitorExpression {
	
	protected final ExpressionPool	pool;
		
	protected ExpressionCloner( ExpressionPool pool )	{ this.pool = pool;	}
	
	public Expression onChoice( ChoiceExp exp ) {
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return pool.createChoice(np1,np2);
	}
	public Expression onOneOrMore( OneOrMoreExp exp ) {
		Expression np = exp.exp.visit(this);
		if(exp.exp==np)		return exp;
		else				return pool.createOneOrMore(np);
	}
	public Expression onMixed( MixedExp exp ) {
		Expression body = exp.exp.visit(this);
		if(exp.exp==body)		return exp;
		else					return pool.createMixed( body );
	}
	public Expression onList( ListExp exp ) {
		Expression body = exp.exp.visit(this);
		if(exp.exp==body)		return exp;
		else					return pool.createList( body );
	}
	public Expression onSequence( SequenceExp exp ) {
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return pool.createSequence(np1,np2);
	}
	public Expression onConcur( ConcurExp exp ) {
		return pool.createConcur(
			exp.exp1.visit(this), exp.exp2.visit(this));
	}
	public Expression onInterleave( InterleaveExp exp ) {
		return pool.createInterleave(
			exp.exp1.visit(this), exp.exp2.visit(this));
	}
	
			
	public Expression onEpsilon()	{ return Expression.epsilon; }
	public Expression onNullSet()	{ return Expression.nullSet; }
	public Expression onAnyString()	{ return Expression.anyString; }
	public Expression onTypedString( TypedStringExp exp ) {
		return exp;
	}
}
