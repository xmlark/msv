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

import com.sun.msv.grammar.Expression;

/**
 * represents a derivation relationship between two classes.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SuperClassItem extends JavaItem {
	public SuperClassItem() {
		super("superClass-marker");
	}
	
	public SuperClassItem( Expression exp ) {
		this();
		this.exp=exp;
	}
	
	/** actual super class definition. */
	public ClassItem definition = null;

	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onSuper(this);
	}
}
