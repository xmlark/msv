/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.tranquilo.datatype;

/** value object of QName.
 * 
 * @author Kohsuke KAWAGUCHI
 */
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
	
	public String toString()
	{
		return "{"+namespaceURI+"}:"+localPart;
	}
	
	QnameValueType( String uri, String localPart )
	{
		this.namespaceURI	= uri;
		this.localPart		= localPart;
	}
}