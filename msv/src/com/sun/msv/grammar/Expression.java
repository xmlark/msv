package com.sun.tranquilo.grammar;

import org.xml.sax.*;
import java.util.Collection;

/**
 * Primitive of regular expression.
 * 
 * most of the derived class is immutable (except ReferenceExp, ElementExp).
 * 
 * By making it immutable, it becomes possible to share subexpressions among expressions.
 * This is very important for regular-expression-derivation based validation algorithm,
 * as well as smaller memory footprint.
 * This sharing is automatically achieved by ExpressionPool.
 * 
 * ReferebceExp is also shared, but its unification is based on its 'name',
 * and its life-cycle is controlled by grammar object.
 * 
 * ElementExp is not shared and therefore is not unified. This is due to the difference
 * of RELAX and TREX in handling tag name constraint. Note that
 * ElementExps are by their nature rarely can be shared; sharing is only possible
 * when they have exactly same content model, attribute constraint and tag name
 * constraint. In case of well-written grammar, this will never happens.
 * 
 * equals method must be implemented by the derived type. equals method will be
 * used to unify the expressions. equals method can safely assume that its children
 * are already unified (therefore == can be used to test the equality, rather than
 * equals method).
 */
public abstract class Expression
{
	/** cached value of epsilon reducibility.
	 * 
	 * Epsilon reducibility can only be calculated after parsing the entire expression,
	 * because of forward reference to other pattern.
	 */
	private Boolean epsilonReducibility;
	
	/** returns true if this expression accepts "".
	 * 
	 * This method is called when creating Expressions, then this method
	 * may return approximated value. When this method is used while validation,
	 * this method is guaranteed to return the correct value.
	 */
	public boolean isEpsilonReducible()
	{
		if( epsilonReducibility==null )
			epsilonReducibility = calcEpsilonReducibility()?Boolean.TRUE:Boolean.FALSE;
		
		return epsilonReducibility==Boolean.TRUE;
	}
	
	/** computes epsilon reducibility */
	protected abstract boolean calcEpsilonReducibility();
	
	protected Expression( int hashCode )
	{
		this.cachedHashCode = hashCode;
	}
	
	/**
	 * this field can be used by Verifier implementation to speed up
	 * validation.
	 */
	public Object verifierTag = null;
	
	
	public abstract Object visit( ExpressionVisitor visitor );
	public abstract Expression visit( ExpressionVisitorExpression visitor );
	public abstract boolean visit( ExpressionVisitorBoolean visitor );
	public abstract void visit( ExpressionVisitorVoid visitor );
	
// if you don't need RELAX capability at all, cut these lines
	public Object visit( com.sun.tranquilo.grammar.relax.RELAXExpressionVisitor visitor )
	{ return visit((ExpressionVisitor)visitor); }
	public Expression visit( com.sun.tranquilo.grammar.relax.RELAXExpressionVisitorExpression visitor )
	{ return visit((ExpressionVisitorExpression)visitor); }
	public boolean visit( com.sun.tranquilo.grammar.relax.RELAXExpressionVisitorBoolean visitor )
	{ return visit((ExpressionVisitorBoolean)visitor); }
	public void visit( com.sun.tranquilo.grammar.relax.RELAXExpressionVisitorVoid visitor )
	{ visit((ExpressionVisitorVoid)visitor); }
// until here
	
	/** hash code of this object.
	 * 
	 * To memorize every sub expression, hash code is frequently used.
	 * And computation of the hash code requires full-traversal of
	 * the expression. Therefore, hash code is computed when the object
	 * is constructed, and kept cached thereafter.
	 */
	private final int cachedHashCode;
	
	public final int hashCode()
	{
		return cachedHashCode;
	}
	
	public abstract boolean equals( Object o );
	
	static protected int hashCode( Object o1, Object o2, int hashKey )
	{
		// TODO: more efficient hashing algorithm
		return o1.hashCode()+o2.hashCode()+hashKey;
	}

	static protected int hashCode( Object o, int hashKey )
	{
		// TODO: more efficient hashing algorithm
		return o.hashCode()+hashKey;
	}
	
	static final int HASHCODE_ATTRIBUTE =		1;
	static final int HASHCODE_CHOICE =			2;
	static final int HASHCODE_ONE_OR_MORE =		3;
	static final int HASHCODE_REF =				4;
	static final int HASHCODE_SEQUENCE =		5;
	static final int HASHCODE_TYPED_STRING =	6;
	static final int HASHCODE_ANYSTRING =		7;
	static final int HASHCODE_EPSILON =			8;
	static final int HASHCODE_NULLSET =			9;
	static final int HASHCODE_ELEMENT =			10;
	static final int HASHCODE_MIXED =			11;
	
	// values for TREX pattern
	// TODO: move them somewhere else
	protected static final int HASHCODE_CONCUR =			20;
	protected static final int HASHCODE_INTERLEAVE =		21;
	
	private static class EpsilonExpression extends Expression
	{
		EpsilonExpression() { super(Expression.HASHCODE_EPSILON); }
		public Object visit( ExpressionVisitor visitor )				{ return visitor.onEpsilon(); }
		public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onEpsilon(); }
		public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onEpsilon(); }
		public void visit( ExpressionVisitorVoid visitor )				{ visitor.onEpsilon(); }
		protected boolean calcEpsilonReducibility() { return true; }
		public boolean equals( Object o ) { return this==o; }	// this class is used as singleton.
	};
	public static final Expression epsilon = new EpsilonExpression();
	
	private static class NullSetExpression extends Expression
	{
		NullSetExpression() { super(Expression.HASHCODE_NULLSET); }
		public Object visit( ExpressionVisitor visitor )				{ return visitor.onNullSet(); }
		public Expression visit( ExpressionVisitorExpression visitor )	{ return visitor.onNullSet(); }
		public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onNullSet(); }
		public void visit( ExpressionVisitorVoid visitor )				{ visitor.onNullSet(); }
		protected boolean calcEpsilonReducibility() { return false; }
		public boolean equals( Object o ) { return this==o; }	// this class is used as singleton.
	};
	public static final Expression nullSet = new NullSetExpression();
	
	private static class AnyStringExpression extends Expression
	{
		AnyStringExpression() { super(Expression.HASHCODE_ANYSTRING); }
		public Object visit( ExpressionVisitor visitor )				{ return visitor.onAnyString(); }
		public Expression visit( ExpressionVisitorExpression visitor ) { return visitor.onAnyString(); }
		public boolean visit( ExpressionVisitorBoolean visitor )		{ return visitor.onAnyString(); }
		public void visit( ExpressionVisitorVoid visitor )				{ visitor.onAnyString(); }
		// anyString is consider to be epsilon reducible.
		// In other words, one can always ignore anyString.
		// 
		// Instead, anyString will remain in the expression even after
		// consuming some StringToken.
		// That is, residual of anyString by StringToken is not the epsilon but an anyString.
		protected boolean calcEpsilonReducibility() { return true; }
		public boolean equals( Object o ) { return this==o; }	// this class is used as singleton.
	};
	public static final Expression anyString = new AnyStringExpression();
}
