/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package schemaLookup;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Enumerates all the namespaces from a DOM tree.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class NamespaceScanner {
    public static void scan( Document d, NamespaceReceiver r ) {
        scan(d.getDocumentElement(),r);
    }
    public static void scan( Element e, NamespaceReceiver r ) {
        new NamespaceScanner(r).scan(e);
    } 
    
    private final Set nss = new HashSet();
    private final NamespaceReceiver receiver;
    
    private NamespaceScanner( NamespaceReceiver r ) {
        this.receiver = r;
        // xmlns
        nss.add("http://www.w3.org/2000/xmlns/");
    }
    
    private void scan( Element e ) {
        onNamespace(e.getNamespaceURI());
        
        NamedNodeMap atts = e.getAttributes();
        for( int i=0; i<atts.getLength(); i++ )
            onNamespace(atts.item(i).getNamespaceURI());
        
        for( int i=0; i<e.getChildNodes().getLength(); i++ ) {
            Node n = e.getChildNodes().item(i);
            if( n instanceof Element )
                scan( (Element)n );
        }
    }
    
    private void onNamespace( String ns ) {
        if(ns==null)    ns="";
        if(nss.add(ns))
            receiver.onNamespace(ns);
    }
}
