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

public class SuperClassItem extends JavaItem {
	public SuperClassItem() {
		super("superClass-marker");
	}
	
	/** actual super class definition. */
	public ClassItem definition = null;

	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onSuper(this);
	}
}
