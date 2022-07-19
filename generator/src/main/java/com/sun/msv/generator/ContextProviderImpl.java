/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */

package com.sun.msv.generator;

import org.relaxng.datatype.ValidationContext;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import com.sun.msv.datatype.SerializationContext;

/**
 * dummy implementation of ValidationContextProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class ContextProviderImpl
	implements ValidationContext, SerializationContext {
	
	public ContextProviderImpl( Element parent ) {
		this.element = parent;
	}
	
	protected final Element element;
	
	public String getNamespacePrefix( String uri ) {
		// find the already declared prefix.
		String prefix = findPredeclaredPrefix(element,uri);
		if(prefix!=null)	return prefix;
		
		// make sure that this prefix is not in use.
		int cnt=1;
		while( resolvePrefix(element,"qns"+cnt)!=null )		cnt++;
		
		// declare attribute
		element.setAttributeNS( XMLNS_URI, "xmlns:qns"+cnt, uri );
		return "qns"+cnt;
	}
	
	public String resolveNamespacePrefix( String prefix ) {
		return resolvePrefix(element,prefix);
	}
	
	public boolean isUnparsedEntity( String name ) {
		// accept anything.
		// ENTITY is used with enumeration, so again
		// this implementation is not a problem.
		return true;
	}
	public boolean isNotation( String name ) {
		// accept anything.
		return true;
	}

	public String getBaseUri() { return null; }
	
	public static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	
	/**
	 * finds a prefix for this URI. If no prefix is declared for this URI,
	 * returns null.
	 */
	protected static String findPredeclaredPrefix( Element e, String uri ) {
		NamedNodeMap m = e.getAttributes();
		for( int i=0; i<m.getLength(); i++ ) {
			Attr a = (Attr)m.item(i);
			if(a.getNamespaceURI().equals(XMLNS_URI)
			&& a.getValue().equals(uri)) {
				int idx = a.getName().indexOf(':');
				if(idx<0)	return "";	// default mapping
				else		return a.getName().substring(idx+1);
			}
		}
		// not found. try parent
		if( e.getParentNode() instanceof Element )
			return findPredeclaredPrefix( (Element)e.getParentNode(), uri );
		return null;	// not found
	}
	
	protected static String resolvePrefix( Element e, String prefix ) {
		String qName = prefix.equals("")?"xmlns":("xmlns:"+prefix);
		
		if(e.getAttributeNode(qName)!=null)
			return e.getAttribute(qName);	// find it.
		
		// not found. try parent
		if( e.getParentNode() instanceof Element )
			return resolvePrefix( (Element)e.getParentNode(), prefix );
		return null;	// not found
	}
}
