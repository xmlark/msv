/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.grammar;

/**
 * Base class for application-specific AGM annotation.
 * 
 * <p>
 * This expression should be treated as do-nothing expression.
 * Application can derive this class and use it for annotating AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class OtherExp extends Expression {
	
	/**
	 * child expression.
	 */
	public Expression exp;
	
	public OtherExp() {
		super(null,null);
	}

	public OtherExp( Expression exp ) {
		this();
		this.exp = exp;
	}
	
	public boolean equals( Object o ) {
		return this==o;
	}
	
	protected boolean calcEpsilonReducibility() {
		if(exp==null)
//			// actual expression is not supplied yet.
//			// actual definition of the referenced expression must be supplied
//			// before any computation over the grammar.
//			throw new Error();	// assertion failed.
			return false;
		// this method can be called while parsing a grammar.
		// in that case, epsilon reducibility is just used for approximation.
		// therefore we can safely return false.
		
		return exp.isEpsilonReducible();
	}
	
	// derived class must be able to behave as a ReferenceExp
	public final Object visit( ExpressionVisitor visitor )				{ return visitor.onOther(this); }
	public final Expression visit( ExpressionVisitorExpression visitor ){ return visitor.onOther(this); }
	public final boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onOther(this); }
	public final void visit( ExpressionVisitorVoid visitor )			{ visitor.onOther(this); }
}
