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
import java.util.Set;

/**
 * completely removes &lt;notAllowed /&gt; from the grammar.
 * 
 * The ExpressionPool class does a reasonable job to remove &lt;notAllowed/&gt;.
 * For example, the createSequence method returns Expression.nullSet if one of the 
 * parameter is the nullSet.
 * 
 * However, it cannot remove elements/attributes whose content model is the nullSet,
 * nor can it remove ReferenceExps whose body is the nullSet. This class walks the
 * grammar and removes those unused ReferenceExps, elements, and attributes.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class NotAllowedRemover extends ExpressionCloner {
	
	public NotAllowedRemover( ExpressionPool pool ) {
		super(pool);
	}
	
	public Expression onRef( ReferenceExp exp ) {
		Expression body = exp.exp.visit(this);
		if(body==Expression.nullSet)
			return Expression.nullSet;
		
		exp.exp=body;
		return exp;
	}
	
	public Expression onOther( OtherExp exp ) {
		Expression body = exp.exp.visit(this);
		if(body==Expression.nullSet)
			return Expression.nullSet;
		
		exp.exp=body;
		return exp;
	}
	
	/**
	 * this set keeps the visited ElementExps/AttributeExps, to prevent
	 * infinite recursion.
	 */
	private final Set visitedExps = new java.util.HashSet();
	
	public Expression onElement( ElementExp exp ) {
		if( !visitedExps.add(exp) )
			return exp;	// this ElementExp is already processed.
		
		Expression body = exp.contentModel.visit(this);
		if( body==Expression.nullSet )
			return Expression.nullSet;
		
		exp.contentModel=body;
		return exp;
	}

	public Expression onAttribute( AttributeExp exp ) {
		if( !visitedExps.add(exp) )
			return exp;	// this AttributeExp is already processed.
		
		Expression body = exp.exp.visit(this);
		if( body==Expression.nullSet )
			return Expression.nullSet;
		
		return pool.createAttribute( exp.nameClass, body, exp.getDefaultValue());
	}	
}
