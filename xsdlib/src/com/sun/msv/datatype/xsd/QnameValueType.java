package com.sun.tranquilo.datatype;


class QnameValueType
{
	String namespaceURI;
	String localPart;
	
	public boolean equals( Object o )
	{
		QnameValueType rhs = (QnameValueType)o;
		
		return namespaceURI.equals(rhs.namespaceURI) && localPart.equals(rhs.localPart);
	}
	
	public int hashCode()
	{
		return namespaceURI.hashCode()+localPart.hashCode();
	}
	
	QnameValueType( String uri, String localPart )
	{
		this.namespaceURI	= uri;
		this.localPart		= localPart;
	}
}