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

import org.relaxng.datatype.Datatype;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.util.StringPair;

/**
 * Expression that matchs characters of the particular {@link DataType}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class TypedStringExp extends Expression {
	
	/** datatype object that actually validates text. */
	public final Datatype dt;
	
	/**
	 * name of this datatype.
	 * 
	 * The value of this field is not considered as significant.
	 * When two TypedStringExps share the same Datatype object,
	 * then they are unified even if they have different names.
	 */
	public final StringPair name;
	
	/**
	 * 'except' clause of RELAX NG.
	 * 
	 * If a token matches this pattern, then it should be rejected.
	 */
	public final Expression except;
	
	protected TypedStringExp( Datatype dt, StringPair typeName, Expression except ) {
		super(hashCode(dt,except,HASHCODE_TYPED_STRING));
		this.dt=dt;
		this.name = typeName;
		this.except = except;
	}
	
	public boolean equals( Object o ) {
		// Note that equals method of this class *can* be sloppy, 
		// since this class does not have a pattern as its child.
		
		// Therefore datatype vocaburary does not necessarily provide
		// strict equals method.
		if(o.getClass()!=this.getClass())	return false;
		
		TypedStringExp rhs = (TypedStringExp)o;
		
		if( this.except != rhs.except )		return false;
		return rhs.dt.equals(dt);
	}
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onTypedString(this); }
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onTypedString(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onTypedString(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onTypedString(this); }

	protected boolean calcEpsilonReducibility() {
		return dt.isValid("",dummyContext);
	}
	
	// At this moment, ValidationContextProvider is used only by QName and ENTITY.
	// And both of them work with the following dummy ValidationContextProvider
	private static final ValidationContext dummyContext =
		new ValidationContext(){
			public boolean isUnparsedEntity( String s )
			{ return s.length()!=0; }
			public String resolveNamespacePrefix( String prefix )
			{ return ""; }
			public boolean isNotation( String s )
			{ return s.length()!=0; }
		};

}
