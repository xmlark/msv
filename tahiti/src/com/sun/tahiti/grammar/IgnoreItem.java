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

/**
 * used to denote the ignored part of the grammar.
 */
public class IgnoreItem extends JavaItem{
	public IgnoreItem() { super("$ignore"); }
}
