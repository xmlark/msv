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

import com.sun.msv.grammar.ReferenceExp;

public class SuperClassItem extends JavaItem {
	public SuperClassItem() {
		super("superClass-marker");
	}
	
	/** actual super class definition. */
	public ClassItem definition = null;
}
