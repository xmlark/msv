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


public interface BindableGrammar extends com.sun.msv.grammar.Grammar {
	LLParserTable getRootTable();
	Object getRootSymbol();
}
