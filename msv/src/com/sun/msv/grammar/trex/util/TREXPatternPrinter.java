/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.trex.util;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;
import com.sun.tranquilo.grammar.trex.*;
import com.sun.tranquilo.grammar.util.ExpressionPrinter;

/**
 * creates a string representation of TREX pattern.
 */
public final class TREXPatternPrinter extends ExpressionPrinter implements TREXPatternVisitor
{
	// singleton access
	public static TREXPatternPrinter fragmentInstance = new TREXPatternPrinter(FRAGMENT);
	public static TREXPatternPrinter contentModelInstance = new TREXPatternPrinter(CONTENTMODEL);
	
	private TREXPatternPrinter( boolean mode ) { super(mode); }
	
	public Object onConcur( ConcurPattern exp )		{ return printBinary(exp,"&"); }
	public Object onInterleave( InterleavePattern exp ){ return printBinary(exp,"^"); }
	public Object onRef( ReferenceExp exp )
	{
		if( mode==FRAGMENT )	return "{"+exp.name+"}";
		else					return exp.exp.visit(this);
	}
	
	public static String printFragment(Expression exp)
	{
		return (String)exp.visit(fragmentInstance);
	}
	public static String printContentModel(Expression exp)
	{
		return (String)exp.visit(contentModelInstance);
	}
}
