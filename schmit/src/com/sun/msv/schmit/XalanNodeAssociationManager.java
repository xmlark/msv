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

import org.apache.xalan.processor.XSLProcessorVersion;
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
            // use specified one.
            try {
                return (XalanNodeAssociationManager)Class.forName(className).newInstance();
            } catch( Exception e ) {
                e.printStackTrace();
                return null;
            }
        } else {
            // guess from the version number of Xalan
            int ver = XSLProcessorVersion.VERSION*100 + XSLProcessorVersion.RELEASE;
            if( Debug.debug )
                System.err.println("Xalan version: "+ver);
            if( ver>202 )
                return new XalanNodeAssociationManager_2_5();
            else
                return new XalanNodeAssociationManager_2_0();
        }
    }
}
