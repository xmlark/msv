/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar;

/**
 * A+.
 *
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class OneOrMoreExp extends UnaryExp
{
	OneOrMoreExp( Expression exp )	{ super( exp,HASHCODE_ONE_OR_MORE ); }
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onOneOrMore(this);	}
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onOneOrMore(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onOneOrMore(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onOneOrMore(this); }

	protected boolean calcEpsilonReducibility()
	{
		return exp.isEpsilonReducible();
	}
}
