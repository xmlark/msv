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

import org.relaxng.datatype.DataType;
import org.relaxng.datatype.ValidationContext;

/**
 * Expression that matchs characters of the particular {@link DataType}.
 * 
 * <p>
 * This class can be extended.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TypedStringExp extends Expression {
	
	/** datatype object that actually validates text. */
	public final DataType dt;
	
	protected TypedStringExp( DataType dt ) {
		super(hashCode(dt,HASHCODE_TYPED_STRING));
		this.dt=dt;
	}
	
	public boolean equals( Object o ) {
		// Note that equals method of this class can be sloppy, 
		// since this class does not have a pattern as its child.
		
		// Therefore datatype vocaburary does not necessarily provide
		// strict equals method.
		if(o.getClass()!=this.getClass())	return false;
		return ((TypedStringExp)o).dt.equals(dt);
	}
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onTypedString(this); }
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onTypedString(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onTypedString(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onTypedString(this); }

	protected boolean calcEpsilonReducibility() {
		return dt.allows("",dummyContext);
	}
	
	// At this moment, ValidationContextProvider is used only by QName and ENTITY.
	// And both of them work with the following dummy ValidationContextProvider
	private static final ValidationContext dummyContext =
		new ValidationContext(){
			public boolean isUnparsedEntity( String s )
			{ return s.length()!=0; }
			public String resolveNamespacePrefix( String prefix )
			{ return ""; }
		};
}
