/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.relax.checker;

import com.sun.tranquilo.grammar.*;
import com.sun.tranquilo.grammar.relax.*;
import java.util.Stack;

/**
 * the purpose of this function object is to make sure
 * that the expression does not contain references to modules
 * other than one specified by this variable.
 */
public final class ExportedHedgeRuleChecker implements RELAXExpressionVisitorBoolean
{
	private final RELAXModule module;
	public ExportedHedgeRuleChecker( RELAXModule module ) { this.module = module; }
	
	/**
	 * traversal stack.
	 * 
	 * This object keeps track of how hedgeRules are visited so that
	 * detailed error message can be provided when an error is found.
	 * 
	 * Say if you start from hr1, hr1 refers hr2, hr2 refers hr3,
	 * and hr3 has a reference to the other module, this stack is
	 * {hr1,hr2,hr3} when an error is found. 
	 */
	private final Stack traversalStack = new Stack();
	
	public ReferenceExp[] errorSnapshot = null;
	
	public boolean onAttribute( AttributeExp exp )		{ return true; }
	public boolean onChoice( ChoiceExp exp )			{ return exp.exp1.visit(this) && exp.exp2.visit(this); }
	public boolean onSequence( SequenceExp exp )		{ return exp.exp1.visit(this) && exp.exp2.visit(this); }
	public boolean onElement( ElementExp exp )			{ return true; }
	public boolean onOneOrMore( OneOrMoreExp exp )		{ return exp.exp.visit(this); }
	public boolean onMixed( MixedExp exp )				{ return exp.exp.visit(this); }
	public boolean onRef( ReferenceExp exp )			{ throw new Error(); }	// should never be called
	public boolean onEpsilon()							{ return true; }
	public boolean onNullSet()							{ return true; }
	public boolean onAnyString()						{ return true; }
	public boolean onTypedString( TypedStringExp exp )	{ return true; }
	public boolean onAttPool( AttPoolClause exp )		{ throw new Error(); }	// should never be called
	public boolean onTag( TagClause exp )				{ throw new Error(); }	// should never be called
	public boolean onElementRules( ElementRules exp )
	{
		if(exp.ownerModule==module)		return true;
		
		takeSnapshot(exp);
		return false;
	}
	public boolean onHedgeRules( HedgeRules exp )
	{
		if( exp.ownerModule!=module )	// reference to the other namespace
		{
			takeSnapshot(exp);
			return false;
		}
		
		traversalStack.push(exp);
		// we have to make sure the same thing for this referenced hedgeRule.
		boolean r = exp.exp.visit(this);
		traversalStack.pop();
		return r;
	}
	
	/**
	 * takes a snap shot of traversal to this.errorSnapshot
	 * so that the user will know what references cause this problem.
	 */
	private void takeSnapshot( ReferenceExp lastExp )
	{
		errorSnapshot = new ReferenceExp[ traversalStack.size()+1 ];
		traversalStack.toArray(errorSnapshot);
		errorSnapshot[errorSnapshot.length-1] = lastExp;
	}
}
