/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.generator;

import com.sun.msv.grammar.*;
import com.sun.msv.grammar.trex.*;
import java.util.Set;

/**
 * collects all distinct element declaration in the grammar.
 * As a side effect, it also collects all distinct attribute declarations.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ElementDeclCollector implements TREXPatternVisitorVoid
{
	public void onEpsilon() {}
	public void onAnyString() {}
	public void onNullSet() {}
	public void onTypedString( TypedStringExp exp ) {}
	
	public void onBinExp( BinaryExp exp )
	{
		exp.exp1.visit(this);
		exp.exp2.visit(this);
	}
	
	public void onChoice( ChoiceExp exp )				{ onBinExp(exp); }
	public void onSequence( SequenceExp exp )			{ onBinExp(exp); }
	public void onInterleave( InterleavePattern exp )	{ onBinExp(exp); }
	public void onConcur( ConcurPattern exp )			{ throw new Error("concur is not supported"); }
	public void onMixed( MixedExp exp )					{ exp.exp.visit(this); }
	public void onOneOrMore( OneOrMoreExp exp )			{ exp.exp.visit(this); }
	
	public void onRef( ReferenceExp exp )				{ exp.exp.visit(this); }
	
	private final Set elements = new java.util.HashSet();
	public void onElement( ElementExp exp )
	{
		if( elements.contains(exp) )	return;	// prevent infinite recursion
		elements.add(exp);
		exp.contentModel.visit(this);
	}
	
	private final Set attributes = new java.util.HashSet();
	public void onAttribute( AttributeExp exp )
	{
		attributes.add(exp);
		exp.exp.visit(this);
	}
	
	private ElementDeclCollector(){}
	
	/**
	 * collects all element and attribute declarations.
	 * 
	 * @return
	 *		r[0] : set of all distinct ElementExps.<br>
	 *		r[1] : set of all distinct AttributeExps.
	 */
	public static Set[] collect( Expression exp )
	{
		Set[] r = new Set[2];
		ElementDeclCollector col = new ElementDeclCollector();
		exp.visit(col);
		
		r[0] = col.elements;
		r[1] = col.attributes;
		return r;
	}
}
