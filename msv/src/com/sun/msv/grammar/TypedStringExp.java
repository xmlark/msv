package com.sun.tranquilo.grammar;

import com.sun.tranquilo.datatype.DataType;
import com.sun.tranquilo.datatype.ValidationContextProvider;

/**
 * Expression that matchs characters of the particular {@link DataType}.
 */
public class TypedStringExp extends Expression
{
	public final DataType dt;
	
	TypedStringExp( DataType dt )
	{
		super(hashCode(dt,HASHCODE_TYPED_STRING));
		this.dt=dt;
	}
	
	public boolean equals( Object o )
	{
		// Note that equals method of this class can be sloppy, 
		// since this class does not have a pattern as its child.
		
		// Therefore datatype vocaburary does not necessarily provide
		// strict equals method.
		if(!(o instanceof TypedStringExp))	return false;
		return ((TypedStringExp)o).dt.equals(dt);
	}
	
	public Object visit( ExpressionVisitor visitor )				{ return visitor.onTypedString(this); }
	public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onTypedString(this); }
	public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onTypedString(this); }
	public void visit( ExpressionVisitorVoid visitor )				{ visitor.onTypedString(this); }

	protected boolean calcEpsilonReducibility()
	{
		return dt.verify("",dummyContext);
	}
	
	// At this moment, ValidationContextProvider is used only by QName and ENTITY.
	// And both of them work with the following dummy ValidationContextProvider
	private static final ValidationContextProvider dummyContext =
		new ValidationContextProvider(){
			public boolean isUnparsedEntity( String s )
			{ return s.length()!=0; }
			public String resolveNamespacePrefix( String prefix )
			{ return ""; }
		};
}
