/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader;

import com.sun.tranquilo.grammar.Expression;

/**
 * state that creates epsion.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class EmptyState extends ExpressionWithoutChildState
{
	protected Expression makeExpression() { return Expression.epsilon; }
}
