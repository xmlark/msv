/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar.trex.util;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.*;
import com.sun.msv.grammar.util.ExpressionPrinter;

/**
 * creates a string representation of TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class TREXPatternPrinter extends ExpressionPrinter implements TREXPatternVisitor {
	// singleton access
	public static TREXPatternPrinter fragmentInstance = new TREXPatternPrinter(FRAGMENT);
	public static TREXPatternPrinter contentModelInstance = new TREXPatternPrinter(CONTENTMODEL);
	public static TREXPatternPrinter smallestInstance = new TREXPatternPrinter(CONTENTMODEL|FRAGMENT);
	
	private TREXPatternPrinter( int mode ) { super(mode); }
	
	public Object onConcur( ConcurPattern exp )		{ return printBinary(exp,"&"); }
	public Object onInterleave( InterleavePattern exp ){ return printBinary(exp,"^"); }
	public Object onRef( ReferenceExp exp ) {
		if( (mode&FRAGMENT)!=0 )		return "{"+exp.name+"}";
		else							return exp.exp.visit(this);
	}
	
	public static String printFragment(Expression exp) {
		return (String)exp.visit(fragmentInstance);
	}
	public static String printContentModel(Expression exp) {
		return (String)exp.visit(contentModelInstance);
	}
	public static String printSmallest(Expression exp) {
		return (String)exp.visit(smallestInstance);
	}
}
