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
 * used to denote the ignored part of the grammar.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class IgnoreItem extends JavaItem {
	public IgnoreItem() { super("$ignore"); }

	public IgnoreItem( Expression exp ) {
		this();
		this.exp=exp;
	}
	public Object visitJI( JavaItemVisitor visitor ) {
		return visitor.onIgnore(this);
	}
}
