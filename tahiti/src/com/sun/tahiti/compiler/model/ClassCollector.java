package com.sun.tahiti.compiler.model;

import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.grammar.ClassItem;
import com.sun.tahiti.grammar.Type;
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
			onClassItem((ClassItem)exp);
		
		// visit its children.
		super.onRef(exp);
	}
	
	private void onClassItem( ClassItem item ) {
		if(classItems.add(item)) {
			// if this class item was not in the set,
			// check the super class.
			
			// sometimes, the base class by itself does not appear directly
			// in AGM after we remove the super class body.
			// testCases/superClass/exp.rng is one of such examples.
			Type superType = item.getSuperType();
			if( superType instanceof ClassItem )
				onClassItem( (ClassItem)superType );
		}
	}
}
