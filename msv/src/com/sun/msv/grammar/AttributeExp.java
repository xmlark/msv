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
 * Attribute declaration.
 * 
 * Attribute declaration consists of a NameClass that verifies attribute name
 * and an Expression that verifies attribute value.
 */
public final class AttributeExp extends Expression
{
	/** constraint over attribute name */
	public final NameClass nameClass;
	/** child expression */
	public final Expression exp;
	
	/** use ExpressionPool to create it from outside */
	AttributeExp( NameClass nameClass, Expression exp )
	{// only PatternPool can create the instance
		super( hashCode( nameClass, exp, HASHCODE_ATTRIBUTE ) );
		this.nameClass	= nameClass;
		this.exp		= exp;
	}
	
	public boolean equals( Object o )
	{
		if(!(o instanceof AttributeExp))	return false;
		return ((AttributeExp)o).nameClass.equals(nameClass)
			&& ((AttributeExp)o).exp.equals(exp);
	}
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onAttribute(this);	}
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onAttribute(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onAttribute(this);	}
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onAttribute(this);	}
	
	protected boolean calcEpsilonReducibility()
	{ return false; }
}
