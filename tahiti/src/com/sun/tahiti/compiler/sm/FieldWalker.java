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
 * traverses an exp tree and reports class items with the field name.
 */
abstract class FieldWalker extends ExpressionWalker
{
	FieldWalker( FieldItem currentField ) {
		this.currentField = currentField;
	}
	
	FieldWalker() { this(null); }
	
	private FieldItem currentField;
	
	
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

	protected abstract void findField( FieldItem field, Type child );

	private static void assert( boolean b ) {
		if(!b)	throw new Error();
	}
}
