/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.reader.trex;

import com.sun.msv.reader.RunAwayExpressionChecker;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.trex.TREXPatternVisitorVoid;
import com.sun.msv.grammar.trex.ConcurPattern;
import com.sun.msv.grammar.trex.InterleavePattern;

/**
 * TREX version of {@link RunAwayExpressionChecker}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXRunAwayExpressionChecker
	extends RunAwayExpressionChecker
	implements TREXPatternVisitorVoid
{
	private TREXRunAwayExpressionChecker( GrammarReader reader ) { super(reader); }

	public static void check( GrammarReader reader, Expression exp ) {
		try {
			exp.visit( new TREXRunAwayExpressionChecker(reader) );
		} catch( RuntimeException e ) {
			if(e!=eureka)	throw e;
		}
	}
	
	public void onConcur( ConcurPattern exp )			{ binaryVisit(exp);	}
	public void onInterleave( InterleavePattern exp )	{ binaryVisit(exp);	}
}
