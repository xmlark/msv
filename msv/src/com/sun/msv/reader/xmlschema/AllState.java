/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.xmlschema;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionWithChildState;

/**
 * parses &lt;interleave&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AllState extends ExpressionWithChildState {
	// TODO: only element is allowed in all
	protected Expression castExpression( Expression exp, Expression child ) {
		// first one.
		if( exp==null )		return child;
		return ((XMLSchemaReader)reader).getPool().createInterleave(exp,child);
	}
}
