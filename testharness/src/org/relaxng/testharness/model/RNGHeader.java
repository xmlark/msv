/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package org.relaxng.testharness.model;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * header information.
 * 
 * @author
 *	<a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RNGHeader {
	
	public RNGHeader( Element headerElement ) { this.element = headerElement; }
	
	/**
	 * &lt;header> element.
	 */
	public Element element;
	
	/**
	 * get the value of the &lt;title> element.
	 * 
	 * If no such element exists, return null.
	 */
	public String getTitle() {
		NodeList lst = element.getElementsByTagName("title");
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
