package com.sun.tranquilo.schema;

import java.util.Iterator;

/**
 * Iterator that merges two Iterator
 */
class MergeIterator implements Iterator
{
	final Iterator a,b;
	
	MergeIterator( Iterator aa, Iterator bb )
	{
		a=aa; b=bb;
	}
	
	public boolean hasNext()
	{
		return a.hasNext() || b.hasNext();
	}
	
	public Object next()
	{
		if(a.hasNext())		return a.next();
		else				return b.next();
	}
	
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
