/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.grammar.relax;

import com.sun.tranquilo.grammar.Expression;
import com.sun.tranquilo.grammar.ReferenceExp;

/**
 * 'attPool'  of RELAX module.
 * 
 * ReferenceExp.exp contains a sequence of AttributeExp.
 */
public class AttPoolClause extends ReferenceExp
{
	protected AttPoolClause( String role )	{ super(role); }
	
	public Object visit( RELAXExpressionVisitor visitor )
	{ return visitor.onAttPool(this); }

	public Expression visit( RELAXExpressionVisitorExpression visitor )
	{ return visitor.onAttPool(this); }
	
	public boolean visit( RELAXExpressionVisitorBoolean visitor )
	{ return visitor.onAttPool(this); }

	public void visit( RELAXExpressionVisitorVoid visitor )
	{ visitor.onAttPool(this); }
}