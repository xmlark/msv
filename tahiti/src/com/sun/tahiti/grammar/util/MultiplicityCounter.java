/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar.util;

import com.sun.msv.grammar.*;
import com.sun.tahiti.grammar.*;

/**
 * visits an expression and computes the multiplicity of the designated child item.
 * 
 * Note that currently this method does not do anything to stop infinite recursion.
 * The algorithm should be implemented within this class to handle recursive multiplicity
 * calculation.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class MultiplicityCounter implements ExpressionVisitor
{
	/**
	 * computes the multiplicity of child Java items.
	 */
	public static final MultiplicityCounter javaItemCounter =
		new MultiplicityCounter(){
			protected Multiplicity isChild( Expression exp ) {
				if(exp instanceof IgnoreItem) return Multiplicity.zero;
				if(exp instanceof JavaItem)	return Multiplicity.one;
				else						return null;
			}
		};
	
	/**
	 * this method should be implemented for this class to work correctly.
	 * If the specified expression is a designated child item, then
	 * return its multiplicity. Otherwise return null.
	 */
	protected abstract Multiplicity isChild( Expression exp );
	
	public Object onSequence( SequenceExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		
		return Multiplicity.group(
			(Multiplicity)exp.exp1.visit(this),
			(Multiplicity)exp.exp2.visit(this)
		);
	}
	
	public Object onInterleave( InterleaveExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		
		return Multiplicity.group(
			(Multiplicity)exp.exp1.visit(this),
			(Multiplicity)exp.exp2.visit(this)
		);
	}
	
	public Object onChoice( ChoiceExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		
		return Multiplicity.choice(
			(Multiplicity)exp.exp1.visit(this),
			(Multiplicity)exp.exp2.visit(this)
		);
	}
	
	public Object onOneOrMore( OneOrMoreExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		return Multiplicity.oneOrMore( (Multiplicity)exp.exp.visit(this) );
	}
	
	public Object onMixed( MixedExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		return exp.exp.visit(this);
	}
	
	public Object onList( ListExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		return exp.exp.visit(this);
	}
	
	public Object onKey( KeyExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null)	return m;
		return exp.exp.visit(this);
	}
	
	public Object onEpsilon() {
		Multiplicity m = isChild(Expression.epsilon);
		if(m==null) m=Multiplicity.zero;
		return m;
	}
	
	public Object onAnyString() {
		Multiplicity m = isChild(Expression.anyString);
		if(m==null) m=Multiplicity.zero;
		return m;
	}
	
	public Object onTypedString( TypedStringExp exp ) {
		Multiplicity m = isChild(exp);
		if(m==null) m=Multiplicity.zero;
		return m;
	}
	
	public Object onElement( ElementExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null) return m;
		return exp.contentModel.visit(this);
	}
	
	public Object onAttribute( AttributeExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null) return m;
		return exp.exp.visit(this);
	}
	
	public Object onRef( ReferenceExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null) return m;
		return exp.exp.visit(this);
	}

	public Object onOther( OtherExp exp ) {
		Multiplicity m = isChild(exp);
		if(m!=null) return m;
		return exp.exp.visit(this);
	}
	
// assertions. these method may never be called.
	public Object onConcur( ConcurExp exp ) { throw new Error(); }
	public Object onNullSet()				{ throw new Error(); }
}
