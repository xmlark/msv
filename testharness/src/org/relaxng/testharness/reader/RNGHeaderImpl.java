/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.reader;

import org.relaxng.testharness.model.RNGHeader;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * header information implemented by &lt;header> element
 * in the test suite file.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
class RNGHeaderImpl implements RNGHeader {
	
	public RNGHeaderImpl( Element headerElement ) { this.element = headerElement; }
	
	/**
	 * &lt;header> element.
	 */
	public Element element;
	
	/**
	 * Gets the value of the &lt;title> element.
	 * 
	 * If no such element exists, return null.
	 */
	public String getName() {
		String title = getProperty(null,"title");
		if(title!=null)	return title;
		
		return getProperty("http://purl.org/dc/elements/1.1/","title");
	}

	public String getProperty( String uri, String local )  {
		if(uri==null || uri.equals(""))
			return _get(element.getElementsByTagName(local));
		else
			return _get(element.getElementsByTagNameNS(uri,local));
	}
	
	private String _get( NodeList lst ) {
		if(lst.getLength()==0)	return null;
		
		lst = lst.item(0).getChildNodes();
		StringBuffer buf = new StringBuffer();
		
		for( int i=0; i<lst.getLength(); i++ ) {
			if( lst.item(i).getNodeType()==Element.TEXT_NODE )
				buf.append( lst.item(i).getNodeValue() );
		}
		
		return buf.toString().trim();
	}
}
