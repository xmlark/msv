/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit;

import java.util.HashMap;
import java.util.Map;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.w3c.dom.Node;

/**
 * Implementation for Xalan 2.5.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class XalanNodeAssociationManager_2_5 extends XalanNodeAssociationManager {
    
    private static final class Key {
        private final DTM dtm;
        private final int index;
        
        private Key( DTMNodeProxy proxy ) {
            this.dtm = proxy.dtm;
            this.index = proxy.getDTMNodeNumber();
        }
        
        public boolean equals(Object obj) {
            if(!(obj instanceof Key))   return false;
            Key rhs = (Key)obj;
            return this.dtm==rhs.dtm && this.index==rhs.index;
        }

        public int hashCode() {
            return dtm.hashCode() ^ index;
        }
    }
    
    /** Actual data store. */
    private final Map store = new HashMap();
    
    public Object get(Node key) {
        return store.get( new Key((DTMNodeProxy)key) );
    }

    public void put(Node key, Object value) {
        store.put( new Key((DTMNodeProxy)key), value );
    }

}
