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

    private static int detectXalanVersion() {
        try {
            int major = XSLProcessorVersion.class.getField("VERSION").getInt(null);
            int minor = XSLProcessorVersion.class.getField("RELEASE").getInt(null);
            return major * 100 + minor;
        } catch (Exception ignored) {
            // Xalan 2.7.3 removed VERSION/RELEASE constants, use version string fallback.
        }

        try {
            String version = XSLProcessorVersion.getVersion();
            if (version != null) {
                int idx = version.indexOf("2.");
                if (idx >= 0 && version.length() > idx + 2) {
                    int pos = idx + 2;
                    int minor = 0;
                    while (pos < version.length() && Character.isDigit(version.charAt(pos))) {
                        minor = (minor * 10) + (version.charAt(pos) - '0');
                        pos++;
                    }
                    return 200 + minor;
                }
            }
        } catch (Exception ignored) {
            // Best effort detection; default to modern implementation below.
        }

        return Integer.MAX_VALUE;
    }
    
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
            int ver = detectXalanVersion();
            if( Debug.debug )
                System.err.println("Xalan version: "+ver);
            if( ver>202 )
                return new XalanNodeAssociationManager_2_5();
            else
                return new XalanNodeAssociationManager_2_0();
        }
    }
}
