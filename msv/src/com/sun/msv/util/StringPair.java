package com.sun.tranquilo.util;

/** pair of Strings */
public final class StringPair
{
	public final String namespaceURI;
	public final String localName;
	public StringPair( String ns, String ln ) { namespaceURI=ns; localName=ln; }
	public boolean equals( Object o )
	{
		return namespaceURI.equals(((StringPair)o).namespaceURI)
			&& localName.equals(((StringPair)o).localName);
	}
	public int hashCode() { return namespaceURI.hashCode()^localName.hashCode(); }
}
