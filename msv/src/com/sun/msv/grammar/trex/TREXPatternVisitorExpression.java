/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex;

import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.Expression;

/**
 * TREX version of {@link ExpressionVisitorExpression}.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TREXPatternVisitorExpression extends ExpressionVisitorExpression
{
	Expression onConcur( ConcurPattern p );
	Expression onInterleave( InterleavePattern p );
}
