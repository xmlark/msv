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
 * TREX version of {@link ExpressionVisitorVoid}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public interface TREXPatternVisitorVoid extends ExpressionVisitorVoid
{
	void onConcur( ConcurPattern p );
	void onInterleave( InterleavePattern p );
}
