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

import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.util.ExpressionWalker;
import com.sun.tahiti.grammar.*;
import java.util.Set;

/**
 * collects all ClassItems and InterfaceItems that can be reached
 * from the specified expression.
 */
public class ClassCollector extends ExpressionWalker {
	
	/** set of ClassItems. */
	public final Set classItems = new java.util.HashSet();
	
	/** set of InterfaceItems. */
	public final Set interfaceItems = new java.util.HashSet();
	
	/** set of all visited Expressions. used to prevent infinite recursion. */
	private final Set visitedExps = new java.util.HashSet();
	
	public void onRef( ReferenceExp exp ) {
		if(!visitedExps.add(exp))	return;
		super.onRef(exp);
	}
	public void onOther( OtherExp exp ) {
		if( !visitedExps.add(exp) )	return;
		
		if( exp instanceof ClassItem )
			onClassItem((ClassItem)exp);
		
		if( exp instanceof InterfaceItem )
			onInterfaceItem((InterfaceItem)exp);
			
		// visit its children.
		super.onOther(exp);
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
	
	private void onInterfaceItem( InterfaceItem item ) {
		interfaceItems.add(item);
	}
}
