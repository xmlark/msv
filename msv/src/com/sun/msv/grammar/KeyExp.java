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

import com.sun.msv.util.StringPair;

/**
 * &lt;key&gt;/&lt;keyref&gt; of RELAX NG.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class KeyExp extends UnaryExp {
	
	protected KeyExp( Expression body, StringPair name, boolean isKey )	{
		super( body, HASHCODE_KEY );
		this.name = name;
		this.isKey = isKey;
	}
	
	public boolean equals( Object o ) {
		if( !this.getClass().equals(o.getClass()) )		return false;
		
		KeyExp rhs = (KeyExp)o;
		
		return this.name.equals(rhs.name) && this.isKey==rhs.isKey;
	}

	/** symbol space of this key. */
	public final StringPair name;
	
	/**
	 * name of the underlying datatype.
	 * 
	 * this value should be computed by the grammar parser, and the consistency
	 * should be also checked by the parser.
	 */
	public transient StringPair dataTypeName;
	
	/** true if this is a key, false if this is a keyref. */
	public final boolean isKey;
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onKey(this);	}
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onKey(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onKey(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onKey(this); }

	protected boolean calcEpsilonReducibility() {
		// even if the body of the key is epsilon-reducible,
		// we don't accept epsilon-reducible tokens for keys.
		return false;
	}
}
