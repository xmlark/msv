package com.sun.tranquilo.grammar;

/**
 * NameClass that matchs any names in a particular namespace.
 */
public class NamespaceNameClass implements NameClass
{
	public final String	namespaceURI;
	
	public boolean accepts( String namespaceURI, String localName )
	{
		if( NAMESPACE_WILDCARD.equals(namespaceURI) )	return true;
		return this.namespaceURI.equals(namespaceURI);
	}
	
	public NamespaceNameClass( String namespaceURI )
	{
		this.namespaceURI	= namespaceURI;
	}
	
	public String toString()
	{
		return namespaceURI+":*";
	}
}
