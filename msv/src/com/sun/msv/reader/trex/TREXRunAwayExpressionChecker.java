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
import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.trex.TREXPatternVisitorVoid;
import com.sun.tranquilo.grammar.trex.ConcurPattern;
import com.sun.tranquilo.grammar.trex.InterleavePattern;

/**
 * TREX version of {@link RunAwayExpressionChecker}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXRunAwayExpressionChecker
	extends RunAwayExpressionChecker
	implements TREXPatternVisitorVoid
{
	private TREXRunAwayExpressionChecker( TREXGrammarReader reader ) { super(reader); }

	public static void check( TREXGrammarReader reader, Expression exp ) {
		try {
			exp.visit( new TREXRunAwayExpressionChecker(reader) );
		} catch( RuntimeException e ) {
			if(e!=eureka)	throw e;
		}
	}
	
	public void onConcur( ConcurPattern exp )			{ binaryVisit(exp);	}
	public void onInterleave( InterleavePattern exp )	{ binaryVisit(exp);	}
}
