/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.util;

import com.sun.tranquilo.grammar.*;

/**
 * creates a string representation of the expression.
 * 
 * performance? who cares....
 */
public abstract class ExpressionPrinter implements ExpressionVisitor
{
	/** in this mode, reference to other expression is
	 * one of the terminal symbol of stringnization.
	 * 
	 * Suitable to dump the entire grammar
	 */
	public final static boolean FRAGMENT = true;
	
	/** in this mode, element declaration is
	 * one of the terminal symbol of stringnization.
	 * 
	 * Suitable to dump the content model of element declarations.
	 */
	public final static boolean CONTENTMODEL = false;
	
	/** this flag controls how expression will be stringnized */
	protected final boolean mode;
	
	protected ExpressionPrinter( boolean mode ) { this.mode = mode; }
	
	/** dumps all the contents of ReferenceContainer.
	 * 
	 * this method is a useful piece to dump the entire grammar.
	 */
	public String printRefContainer( ReferenceContainer cont )
	{
		String r="";
		java.util.Iterator itr = cont.iterator();
		while( itr.hasNext() )
		{
			ReferenceExp exp = (ReferenceExp)itr.next();
			
			r += exp.name + "  : " + exp.exp.visit(this) + "\n";
		}
		return r;
	}
	
	/** determines whether brackets should be used to represent the pattern */
	protected static boolean isComplex( Expression exp )
	{
		return exp instanceof BinaryExp;
	}
	
	protected String printBinary( BinaryExp exp, String op )
	{
		String r;
		
		if( isComplex(exp.exp1) )	r="("+exp.exp1.visit(this)+")";
		else						r=(String)exp.exp1.visit(this);
		
		r+=op;
		
		if( exp.exp2.getClass()==exp.getClass() || !isComplex(exp.exp2) )
				r+=exp.exp2.visit(this);
		else
				r+="("+exp.exp2.visit(this)+")";
		
		return r;
	}
	
	public Object onAttribute( AttributeExp exp )
	{
		return "@"+exp.nameClass.toString()+"<"+exp.exp.visit(this)+">";
	}
	private Object optional( Expression exp )
	{
		if( exp instanceof OneOrMoreExp )
		{
			OneOrMoreExp ome = (OneOrMoreExp)exp;
			if( isComplex(ome.exp) )	return "("+ome.exp.visit(this)+")*";
			else						return ome.exp.visit(this)+"*";
		}
		else
		{
			if( isComplex(exp) )	return "("+exp.visit(this)+")?";
			else					return exp.visit(this)+"?";
		}
	}
	public Object onChoice( ChoiceExp exp )	
	{
		if( exp.exp1==Expression.epsilon )	return optional(exp.exp2);
		if( exp.exp2==Expression.epsilon )	return optional(exp.exp1);
			
		return printBinary(exp,"|");
	}
	public Object onElement( ElementExp exp )
	{
		if( mode==FRAGMENT )
			return exp.getNameClass().toString()+"<"+exp.contentModel.visit(this)+">";
		else
			return exp.getNameClass().toString();
	}
	public Object onOneOrMore( OneOrMoreExp exp )
	{
		if( isComplex(exp.exp) )	return "("+exp.exp.visit(this)+")+";
		else						return exp.exp.visit(this)+"+";
	}
	public Object onMixed( MixedExp exp )
	{
		return "mixed["+exp.exp.visit(this)+"]";
	}
	public Object onEpsilon()
	{
		return "#epsilon";
	}
	public Object onNullSet()
	{
		return "#nullSet";
	}
	public Object onAnyString()
	{
		return "<anyString>";
	}
	public Object onSequence( SequenceExp exp )	{ return printBinary(exp,","); }
	public Object onTypedString( TypedStringExp exp )
	{
		return "$"+exp.dt.displayName();
	}	
}
