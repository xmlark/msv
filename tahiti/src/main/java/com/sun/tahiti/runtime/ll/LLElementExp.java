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

import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.ElementPattern;

/**
 * ElementExp with LL parser table.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LLElementExp extends ElementPattern {
	/** LL parser table for this rule. */
	public LLParserTable parserTable;
	
	public LLElementExp( NameClass nc ) {
		this(nc,Expression.nullSet);
	}

	public LLElementExp( NameClass nc, Expression body ) {
		super(nc,body);
	}
}
