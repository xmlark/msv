package com.sun.tranquilo.grammar;

/**
 * Base implementation for those expression which has two child expressions.
 */
public abstract class BinaryExp extends Expression
{
	public final Expression exp1;
	public final Expression exp2;
	
	public BinaryExp( Expression left, Expression right, int hashKey )
	{
		super( hashCode(left,right,hashKey) );
		this.exp1 = left;
		this.exp2 = right;
	}

	public boolean equals( Object o )
	{
		if( !this.getClass().equals(o.getClass()) )		return false;
		
		// every existing children are already unified.
		// therefore, == is enough. (don't need to call equals)
		return ((BinaryExp)o).exp1 == exp1
			&& ((BinaryExp)o).exp2 == exp2;
	}
}
