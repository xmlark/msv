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

import com.sun.tranquilo.grammar.ExpressionVisitorVoid;

/**
 * TREX version of ExpressionVisitorVoid
 */
public interface TREXPatternVisitorVoid extends ExpressionVisitorVoid
{
	void onConcur( ConcurPattern p );
	void onInterleave( InterleavePattern p );
}
