/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.reader.trex;

import com.sun.tranquilo.reader.RunAwayExpressionChecker;
import com.sun.tranquilo.grammar.trex.TREXPatternVisitorVoid;
import com.sun.tranquilo.grammar.trex.ConcurPattern;
import com.sun.tranquilo.grammar.trex.InterleavePattern;
	
public class TREXRunAwayExpressionChecker
	extends RunAwayExpressionChecker
	implements TREXPatternVisitorVoid
{
	public TREXRunAwayExpressionChecker( TREXGrammarReader reader ) { super(reader); }

	public void onConcur( ConcurPattern exp )			{ binaryVisit(exp);	}
	public void onInterleave( InterleavePattern exp )	{ binaryVisit(exp);	}
}
