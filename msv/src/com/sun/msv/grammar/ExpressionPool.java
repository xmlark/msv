package com.sun.tranquilo.grammar;

import java.util.Hashtable;
import com.sun.tranquilo.datatype.DataType;

/**
 * Creates a new Expression by combining existing expressions.
 * 
 * all expressions are memorized and unified so that every subexpression
 * will be shared and reused.
 * 
 * Although this unification is essential, but this is also the performance
 * bottle neck. In particular, createChoice and createSequence are two most
 * commonly called methods.
 * 
 * For example, when validating a DocBook XML (150KB) twice against
 * DocBook.trex(237KB), createChoice is called 63000 times and createSequence
 * called 23000 times. (the third is createOptional method and 1560 times.)
 * 
 * And they took more than 10% of validation time, which is the worst
 * time-consuming method.
 */
public class ExpressionPool
{
	public Expression createAttribute( NameClass nameClass, Expression content )
	{
		return unify(new AttributeExp(nameClass,content));
	}
	
	public Expression createChoice( Expression left, Expression right )
	{
		if( left==Expression.nullSet )		return right;
		if( right==Expression.nullSet )		return left;
		
		if( left==Expression.epsilon && right.isEpsilonReducible() )	return right;
		if( right==Expression.epsilon && left.isEpsilonReducible() )	return left;
		
		// TODO: should we re-order choice in cconsistent manner?
		
		// associative operators are grouped to the right
		if( left instanceof ChoiceExp )
		{
			final ChoiceExp c = (ChoiceExp)left;
			
			return createChoice( c.exp1, createChoice(c.exp2, right) );
		}

		// eliminate duplicate choice items.
		Expression next = right;
		while( true )
		{
			if( next==left )	return right;	// left is already in the choice
			if(!(next instanceof ChoiceExp))	break;
				
			ChoiceExp cp = (ChoiceExp)next;
			if( cp.exp1==left )	return right;
			next = cp.exp2;
		}

		// special (optimized) unification.
		// this will prevent unnecessary ChoiceExp instanciation.
		Expression o = expTable.get(
				Expression.hashCode(left,right,Expression.HASHCODE_CHOICE),
				left, right, ChoiceExp.class );
		if(o==null)
		{
			o = new ChoiceExp(left,right);
			expTable.put(o);
			return o;
		}
		else
			return o;
	}
	
	public Expression createOneOrMore( Expression child )
	{
		if( child == Expression.epsilon
		||  child == Expression.anyString
		||  child == Expression.nullSet
		||  child instanceof OneOrMoreExp )
			return child;
		
		return unify(new OneOrMoreExp(child));
	}
	
	public Expression createZeroOrMore( Expression child )
	{
		return createOptional(createOneOrMore(child));
	}
	
	public Expression createOptional( Expression child )
	{
		// optimization will be done in createChoice method.
		return createChoice(child,Expression.epsilon);
	}
	
	public Expression createTypedString( DataType dt )
	{
		return unify( new TypedStringExp(dt) );
	}
	
	public Expression createMixed( Expression body )
	{
		if( body==Expression.nullSet )		return Expression.nullSet;
		if( body==Expression.epsilon )		return Expression.anyString;
		
		return unify( new MixedExp(body) );
	}
	
	public Expression createSequence( Expression left, Expression right )
	{
		if( left ==Expression.nullSet
		||	right==Expression.nullSet )	return Expression.nullSet;
		if( left ==Expression.epsilon )	return right;
		if( right==Expression.epsilon )	return left;
		
		// associative operators are grouped to the right
		if( left instanceof SequenceExp )
		{
			final SequenceExp s = (SequenceExp)left;
			
			return createSequence( s.exp1, createSequence(s.exp2, right) );
		}
		
		// special (optimized) unification.
		Expression o = expTable.get(
				Expression.hashCode(left,right,Expression.HASHCODE_SEQUENCE),
				left, right, SequenceExp.class );
		if(o==null)
		{
			o = new SequenceExp(left,right);
			expTable.put(o);
			return o;
		}
		else
			return o;
	}
	
	
	
//	private final Hashtable expTable = new Hashtable();
	private final ClosedHash expTable = new ClosedHash();
	
	/**
	 * unifies expressions.
	 * 
	 * If the equivalent expression is already registered in the table,
	 * destroy newly created one (so that no two objects represents
	 * same expression structure).
	 * 
	 * If it's not registered, then register it and return it.
	 */
	protected Expression unify( Expression exp )
	{
		// TODO: make sure that this is thread-safe
		Object o = expTable.get(exp);
		if(o==null)
		{// expression may not be registered. So try it again with lock
			synchronized(expTable)
			{
				o = expTable.get(exp);
				if(o==null)
				{// expression is not registered.
					expTable.put( exp );
					return exp;
				}
			}
		}
		
		// expression is already registered.
		return (Expression)o;
	}


public final static class ClosedHash
{
	/**
	 * The hash table data.
	 */
	private Expression table[];

	/**
	 * The total number of mappings in the hash table.
	 */
	private int count;

	/**
	 * The table is rehashed when its size exceeds this threshold.  (The
	 * value of this field is (int)(capacity * loadFactor).)
	 */
	private int threshold;

	/**
	 * The load factor for the hashtable.
	 */
	private static final float loadFactor = 0.3f;
	private static final int initialCapacity = 191;
	
	public ClosedHash()
	{
		table = new Expression[initialCapacity];
		threshold = (int)(initialCapacity * loadFactor);
	}

	
	public Expression get( int hash, Expression left, Expression right, Class type )
	{
		Expression tab[] = table;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		
		while(true)
		{
			final Expression e = tab[index];
			if( e==null )		return null;
			if( e.hashCode()==hash && e.getClass()==type )
			{
				BinaryExp be = (BinaryExp)e;
				if( be.exp1==left && be.exp2==right )
					return be;
			}
			index = (index+1)%tab.length;
		}
	}
	public Expression get( int hash, Expression child, Class type )
	{
		Expression tab[] = table;
		int index = (hash & 0x7FFFFFFF) % tab.length;
		
		while(true)
		{
			final Expression e = tab[index];
			if( e==null )		return null;
			if( e.hashCode()==hash && e.getClass()==type )
			{
				UnaryExp ue = (UnaryExp)e;
				if( ue.exp==child )		return ue;
			}
			index = (index+1)%tab.length;
		}
	}
	public Expression get( Expression key )
	{
		Expression tab[] = table;
		int index = (key.hashCode() & 0x7FFFFFFF) % tab.length;
		
		while(true)
		{
			final Expression e = tab[index];
			if( e==null )		return null;
			if( e.equals(key) )	return e;
			index = (index+1)%tab.length;
		}
	}

	private void rehash()
	{
		int oldCapacity = table.length;
		Expression oldMap[] = table;

		int newCapacity = oldCapacity * 2 + 1;
		Expression newMap[] = new Expression[newCapacity];

		threshold = (int)(newCapacity * loadFactor);
		table = newMap;

		for (int i = oldCapacity ; i-- > 0 ;)
			if( oldMap[i]!=null )
			{
				int index = (oldMap[i].hashCode() & 0x7FFFFFFF) % newMap.length;
				while(newMap[index]!=null)
					index=(index+1)%newMap.length;
				newMap[index] = oldMap[i];
			}
	}

	public void put(Expression newExp)
	{
		if (count >= threshold)		rehash();

		Expression tab[] = table;
		int index = (newExp.hashCode() & 0x7FFFFFFF) % tab.length;
		
		while(tab[index]!=null)
			index=(index+1)%tab.length;
		tab[index] = newExp;
		
		count++;
	}

}
	
}
