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

import org.w3c.dom.Node;

/**
 * Implementation for older version of Xalan (2.0-2.1?)
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
final class XalanNodeAssociationManager_2_0 extends XalanNodeAssociationManager {
    
    /** Actual data store. */
    private final Map store = new HashMap();
    
    public Object get(Node key) {
        return store.get( key );
    }

    public void put(Node key, Object value) {
        store.put( key, value );
    }

}
