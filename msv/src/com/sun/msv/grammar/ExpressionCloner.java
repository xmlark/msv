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
 * clones an expression.
 * 
 * This class is used as a default implementation for relevant task.
 */
public class ExpressionCloner implements ExpressionVisitorExpression
{
	protected final ExpressionPool	pool;
		
	protected ExpressionCloner( ExpressionPool pool )	{ this.pool = pool;	}
	
	public Expression onChoice( ChoiceExp exp )
	{
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return pool.createChoice(np1,np2);
	}
	public Expression onOneOrMore( OneOrMoreExp exp )
	{
		Expression np = exp.exp.visit(this);
		if(exp.exp==np)		return exp;
		else				return pool.createOneOrMore(np);
	}
	public Expression onMixed( MixedExp exp )
	{
		Expression body = exp.exp.visit(this);
		if(exp.exp==body)		return exp;
		else					return pool.createMixed( body );
	}
	public Expression onSequence( SequenceExp exp )
	{
		Expression np1 = exp.exp1.visit(this);
		Expression np2 = exp.exp2.visit(this);
		if(exp.exp1==np1 && exp.exp2==np2)	return exp;
		else								return pool.createSequence(np1,np2);
	}
	
			
	public Expression onEpsilon()	{ return Expression.epsilon; }
	public Expression onNullSet()	{ return Expression.nullSet; }
	public Expression onAnyString()	{ return Expression.anyString; }
	public Expression onTypedString( TypedStringExp exp )
	{
		return exp;
	}
	public Expression onAttribute( AttributeExp exp )
	{
		return exp;
	}
	public Expression onElement( ElementExp exp )
	{
		return exp;
	}
	public Expression onRef( ReferenceExp exp )
	{
		return exp;
	}
}
