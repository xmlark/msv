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

import org.w3c.dom.Node;

/**
 * Encapsulates the logic to associate arbitrary objects to Xalan's
 * DTM Node.
 * 
 * <p>
 * Santiago told me that this area of Xalan is changing rapidly,
 * so I put this "dangerous" code into a separate class to isolate it
 * and make it pluggable.
 * 
 * <p>
 * This interface provides a "weak map" that allows the association
 * of schmit-specific objects to Xalan nodes. The implementation of
 * this interface needs to make sure that it won't use storage for
 * {@link Node}s that are no longer used.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class XalanNodeAssociationManager {
    public abstract void put( Node key, Object value );
    public abstract Object get( Node key );
    
    /**
     * Creates a new instance.
     */
    public static final XalanNodeAssociationManager createInstance() {
        String className = null;
        try {
            className = System.getProperty(XalanNodeAssociationManager.class.getName()+".implementation");
        } catch( SecurityException e ) {
            // a security manager might reject this call
        }
        if(className!=null) {
            try {
                return (XalanNodeAssociationManager)Class.forName(className).newInstance();
            } catch( Exception e ) {
                e.printStackTrace();
                return null;
            }
        } else
            return new XalanNodeAssociationManager_2_5();
    }
}
