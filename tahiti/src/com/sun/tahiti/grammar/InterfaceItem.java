/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.grammar;

public class InterfaceItem extends TypeItem {
	public InterfaceItem( String name ) {
		super(name);
	}

	public Type getSuperType() { return null; } // interfaces do not have the super type.

	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onInterface(this);
	}
}
