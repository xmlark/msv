/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tahiti.runtime.ll;

/**
 * LL parser table.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface LLParserTable {
	/**
	 * looks up LL parsing table.
	 * 
	 * @param	symStackTop
	 *		the symbol of the current stack top.
	 * @param	symInput
	 *		the symbol of the current input token.
	 * @return
	 *		null if there is no rule to apply.
	 */
	Rule[] get( Object symStackTop, Object symInput );
}
