/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.*;

/**
 * Creates an expression whose AttributeExp is completely replaced by nullSet.
 * 
 * This step is used to remove all unconsumed AttributeExp from the expression.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributePruner extends ExpressionCloner
{
	public AttributePruner( ExpressionPool pool ) { super(pool); }
	
	public Expression onAttribute( AttributeExp exp )	{ return Expression.nullSet; }
	public Expression onRef( ReferenceExp exp )			{ return exp.exp.visit(this); }
	public Expression onOther( OtherExp exp )			{ return exp.exp.visit(this); }
	public Expression onElement( ElementExp exp )		{ return exp; }
	
	public final Expression prune( Expression exp )
	{
		OptimizationTag ot = (OptimizationTag)exp.verifierTag;
		if(ot==null)	exp.verifierTag = ot = new OptimizationTag();
		else
			if( ot.attributePrunedExpression!=null )
				return ot.attributePrunedExpression;
		
		Expression r = exp.visit(this);
		
		ot.attributePrunedExpression = r;	// cache this result
		return r;
	}
}
