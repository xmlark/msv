/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.generator;

import com.sun.msv.datatype.ValidationContextProvider;
import com.sun.msv.datatype.SerializationContextProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * dummy implementation of ValidationContextProvider.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final public class ContextProvider
	implements ValidationContextProvider, SerializationContextProvider {
	
	public ContextProvider( Element parent ) {
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
			return findPredeclaredPrefix( (Element)e.getParentNode(), prefix );
		return null;	// not found
	}
}
