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

import java.util.Set;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.util.ExpressionWalker;

/**
 * collects all distinct element declaration in the grammar.
 * As a side effect, it also collects all distinct attribute declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclCollector extends ExpressionWalker {
	public void onConcur( ConcurExp exp ) {
		throw new Error("concur is not supported");
	}
	
	private final Set elements = new java.util.HashSet();
	public void onElement( ElementExp exp ) {
		if( elements.contains(exp) )	return;	// prevent infinite recursion
		elements.add(exp);
		super.onElement(exp);
	}
	
	private final Set attributes = new java.util.HashSet();
	public void onAttribute( AttributeExp exp ) {
		attributes.add(exp);
		super.onAttribute(exp);
	}
	
	private ElementDeclCollector(){}
	
	/**
	 * collects all element and attribute declarations.
	 * 
	 * @return
	 *		r[0] : set of all distinct ElementExps.<br>
	 *		r[1] : set of all distinct AttributeExps.
	 */
	public static Set[] collect( Expression exp ) {
		Set[] r = new Set[2];
		ElementDeclCollector col = new ElementDeclCollector();
		exp.visit(col);
		
		r[0] = col.elements;
		r[1] = col.attributes;
		return r;
	}
}
