/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.verifier.regexp;

import com.sun.tranquilo.grammar.*;

/**
 * traverses Expression and marks every parts as either attribute free or not.
 */
public class AttributeFreeMarker implements ExpressionVisitorBoolean
{
	protected OptimizationTag get(Expression exp)
	{
		OptimizationTag ot = (OptimizationTag)exp.verifierTag;
		if(ot==null)	exp.verifierTag = ot = new OptimizationTag();
		return ot;
	}
	
	protected final void mark( Expression exp ) { exp.visit(this); }
	
	public boolean onAttribute( AttributeExp exp )	{ return false; }
	public boolean onChoice( ChoiceExp exp )		{ return onBinExp(exp); }
	public boolean onElement( ElementExp exp )		{ return true; }
	public boolean onOneOrMore( OneOrMoreExp exp )	{ return onUnaryExp(exp); }
	public boolean onMixed( MixedExp exp )			{ return onUnaryExp(exp); }
	public boolean onRef( ReferenceExp exp )		{ return exp.exp.visit(this); }
	public boolean onEpsilon()						{ return true; }
	public boolean onNullSet()						{ return true; }
	public boolean onAnyString()					{ return true; }
	public boolean onSequence( SequenceExp exp )	{ return onBinExp(exp); }
	public boolean onTypedString( TypedStringExp exp ) { return true; }


	
	protected boolean onBinExp( BinaryExp exp )
	{
		OptimizationTag ot = get(exp);
		if( ot.isAttributeFree == null )
		{
			boolean b1 = exp.exp1.visit(this);
			boolean b2 = exp.exp2.visit(this);
			boolean b = b1 && b2;	// we need three sentences to visit both.
			ot.isAttributeFree = b?Boolean.TRUE:Boolean.FALSE;
			return b;
		}
		else
			return ot.isAttributeFree==Boolean.TRUE;
	}
	protected boolean onUnaryExp( UnaryExp exp )
	{
		OptimizationTag ot = get(exp);
		if( ot.isAttributeFree == null )
		{
			boolean b = exp.exp.visit(this);
			ot.isAttributeFree = b?Boolean.TRUE:Boolean.FALSE;
			return b;
		}
		else
			return ot.isAttributeFree==Boolean.TRUE;
	}
}
