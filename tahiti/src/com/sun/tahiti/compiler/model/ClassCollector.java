package com.sun.tahiti.compiler.model;

import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.grammar.ClassItem;
import java.util.Set;

/**
 * collects all ClassItems that can be reached from the specified expression.
 */
public class ClassCollector extends ExpressionWalker {
	
	/** set of all visited ReferenceExps. used to prevent infinite recursion. */
	private final Set visitedRefs = new java.util.HashSet();
	
	/** set of ClassItems. */
	public final Set classItems = new java.util.HashSet();
	
	public void onRef( ReferenceExp exp ) {
		if( !visitedRefs.add(exp) )	return;
		
		if( exp instanceof ClassItem )
			classItems.add(exp);
		
		// visit its children.
		super.onRef(exp);
	}
}
