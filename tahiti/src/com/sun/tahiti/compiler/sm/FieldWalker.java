/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.compiler.sm;

import com.sun.tahiti.grammar.*;
import com.sun.msv.grammar.*;
import com.sun.msv.grammar.util.ExpressionWalker;

/**
 * traverses an expression tree and reports class items with the field name.
 */
abstract class FieldWalker extends ExpressionWalker
{
	FieldWalker( FieldItem currentField ) {
		this.currentField = currentField;
	}
	
	FieldWalker() { this(null); }

	private FieldItem currentField;

	/**
	 * this method is called whenever a ClassItem/PrimitiveItem/InterfaceItem
	 * is found. Note that the same object can be reported more than once,
	 * with possibly the different name (or the same name).
	 * 
	 * @param field
	 *		FieldItem object that encapsulates the 'child' object. In other words,
	 *		the 'child' object is stored in this field.
	 * @param child
	 *		ClassItem, PrimitiveItem, or InterfaceItem object found in
	 *		the expression tree. This is the child object.
	 */
	protected abstract void findField( FieldItem field, Type child );

	
	
	public void onOther( OtherExp exp ) {
		if( exp instanceof FieldItem ) {
			assert(currentField==null);
			currentField = (FieldItem)exp;
			exp.exp.visit(this);
			assert(currentField==exp);
			currentField = null;
			return;
		}
		if( exp instanceof ClassItem || exp instanceof InterfaceItem
		||  exp instanceof PrimitiveItem ) {
			assert(currentField!=null);
			findField( currentField, (Type)exp );
			return;
		}
		if( exp instanceof IgnoreItem )	return;
		assert(!(exp instanceof JavaItem));
		super.onOther(exp);
	}

	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
