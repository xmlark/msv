/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex;

import com.sun.tranquilo.grammar.ExpressionVisitorExpression;
import com.sun.tranquilo.grammar.Expression;

/**
 * TREX version of ExpressionVisitorExpression
 */
public interface TREXPatternVisitorExpression extends ExpressionVisitorExpression
{
	Expression onConcur( ConcurPattern p );
	Expression onInterleave( InterleavePattern p );
}
