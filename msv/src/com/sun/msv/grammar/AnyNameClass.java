package com.sun.tranquilo.grammar;

/**
 * a NameClass that matches any name
 */
public final class AnyNameClass implements NameClass
{
	public boolean accepts( String namespaceURI, String localName )
	{
		return true;
	}
	
	/** singleton instance */
	public static final NameClass theInstance = new AnyNameClass();
	
	private AnyNameClass() {}
	
	public String toString()	{ return "*:*"; }
}
