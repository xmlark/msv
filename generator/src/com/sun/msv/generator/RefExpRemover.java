/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.relax.*;
import com.sun.msv.grammar.trex.*;
import java.util.Iterator;
import java.util.Set;

/**
 * removes all ReferenceExp from AGM.
 * 
 * when named expression is nullSet, it cannot be used.
 * by replacing ReferenceExp by its definition, those unavailable expressions
 * will be properly removed from AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefExpRemover
	extends ExpressionCloner {
	
	/** set of visited ElementExps */
	private final Set visitedElements = new java.util.HashSet();
	
	public RefExpRemover( ExpressionPool pool ) { super(pool); }
	
	public Expression onElement( ElementExp exp )
	{
		if( !visitedElements.contains(exp) )
		{// remove refs from this content model
			visitedElements.add(exp);
			exp.contentModel = exp.contentModel.visit(this);
		}
		if(exp.contentModel==Expression.nullSet)
			return Expression.nullSet;	// this element is not allowed
		else
			return exp;
	}
	public Expression onAttribute( AttributeExp exp )
	{
		Expression content = exp.exp.visit(this);
		if( content==Expression.nullSet )
			return Expression.nullSet;	// this attribute is not allowed
		else
			return pool.createAttribute( exp.nameClass, content );
	}
	public Expression onRef( ReferenceExp exp )
	{
		return exp.exp.visit(this);
	}
}
