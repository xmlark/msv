package com.sun.tranquilo.grammar;

/**
 * NameClass that acts a not operator.
 */
public final class NotNameClass implements NameClass
{
	public final NameClass child;

	public boolean accepts( String namespaceURI, String localName )
	{
		return !child.accepts(namespaceURI,localName);
	}

	public NotNameClass( NameClass child )
	{
		this.child = child;
	}
	
	public String toString()	{ return "~"+child.toString(); }
}
