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
 * Base implementation for those expression who has one child expresison.
 */
public abstract class UnaryExp extends Expression
{
	public final Expression exp;
	
	public UnaryExp( Expression exp, int hashKey )
	{
		super( hashCode(exp,hashKey) );
		this.exp = exp;
	}

	public boolean equals( Object o )
	{
		if( !this.getClass().equals(o.getClass()) )		return false;
		
		// every existing children are already unified.
		// therefore, == is enough. (don't need to call equals)
		return ((UnaryExp)o).exp == exp;
	}
}
