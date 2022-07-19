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

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Decorates DOM with missing information so that it will be nicely serialized.
 */
public class DOMDecorator {
	private static final String XMLNS_URI = "http://www.w3.org/2000/xmlns/";
	
	public static void decorate( Document doc ) {
		// collect namespace URIs that are used.
		final Set usedURIs = new java.util.HashSet();
		// also, collect prefixes that are already in use.
		final Set usedPrefixes = new java.util.HashSet();
		
		Element root = doc.getDocumentElement();
		
		visit( root, new DOMVisitor(){
			public void onElement( Element e ) {
				usedURIs.add(nullAdjust(e.getNamespaceURI()));
			}
			public void onAttr( Attr a ) {
				String uri = nullAdjust(a.getNamespaceURI());
				if(!"".equals(uri) && !uri.equals(XMLNS_URI))
					usedURIs.add(nullAdjust(a.getNamespaceURI()));
				
				if(nullAdjust(a.getNamespaceURI()).equals(XMLNS_URI)) {
					String qname = a.getName();
					int idx = qname.indexOf(':');
					if(idx<0)
						usedPrefixes.add(qname.substring(idx+1));
				}
                
                // compute the value of a child attribute by
                // concatanating child nodes.
                // With Xerces, a.getValue() works but witih Crimson,
                // this doesn't work. So as a workaround, we need to
                // process child nodes and recompute the attribute value here
                StringBuffer buf = new StringBuffer();
                NodeList lst = a.getChildNodes();
                for( int j=0; j<lst.getLength(); j++ )
                    buf.append( lst.item(j).getNodeValue() );
                a.setValue( buf.toString() );
			}
		});
		
		// if an empty namespace "" is not used, then namespace URI of 
		// the root element is considered as the default namespace URI.
		final String defaultNs =
			(!usedURIs.contains(""))?
				nullAdjust(root.getNamespaceURI()):"";
        
		if( !defaultNs.equals("") )	// declare the default ns
			root.setAttributeNS( XMLNS_URI, "xmlns", defaultNs );
		
		// map from prefix to URI.
		final Map uri2prefix = new java.util.HashMap();
		
		// assign prefixes for other URIs.
		int cnt=1;
		String[] uris = (String[])usedURIs.toArray(new String[0]);
		for( int i=0; i<uris.length; i++ ) {
			if( uris[i].equals(defaultNs) ) continue;   // skip.
            
			String prefix;
			do {
				prefix = "ns"+(cnt++);
			} while( uri2prefix.containsValue(prefix) );
			
			uri2prefix.put( uris[i], prefix );
			// add this declaration to the document element.
			root.setAttributeNS( XMLNS_URI, "xmlns:"+prefix, uris[i] );
		}
		
		
		// visit nodes and set prefixes
		visit( root, new DOMVisitor(){
			public void onElement( Element e ) {
                String uri = nullAdjust(e.getNamespaceURI()); 
				if( uri.equals(defaultNs) )
					; // don't touch
				else
					e.setPrefix((String)uri2prefix.get(uri));
			}
			public void onAttr( Attr a ) {
				String uri = nullAdjust(a.getNamespaceURI());
				if("".equals(uri))
					return;	// do nothing.
				
				if(uri.equals(XMLNS_URI))
					return;	// don't touch xmlns declarations.
				
				String prefix = (String)uri2prefix.get(uri);
				a.setPrefix(prefix);
			}
		});
	}
	
	
	/** visits Elements and Attrs by using a DOMVisitor. */
	public static void visit( Element e, DOMVisitor visitor ) {
		visitor.onElement(e);
		// visit attributes
		NamedNodeMap map = e.getAttributes();
		for( int i=0; i<map.getLength(); i++ )
			visitor.onAttr( (Attr)map.item(i) );
		
		// visit children
		NodeList children = e.getChildNodes();
		for( int i=0; i<children.getLength(); i++ ) {
			Node n = children.item(i);
			if( n instanceof Element )
				visit((Element)n,visitor);
		}
	}
	
	static interface DOMVisitor {
		void onElement(Element e);
		void onAttr(Attr a);
	}
	
    private static String nullAdjust(String s) {
        return s==null?"":s;
    }
}
