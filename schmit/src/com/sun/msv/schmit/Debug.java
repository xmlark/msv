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

/**
 * A debug flag.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
class Debug {
    static final boolean debug = getDebugFlag();

    private static boolean getDebugFlag() {
        try {
            return System.getProperty("com.sun.msv.schmit.debug")!=null;
        } catch( Throwable t ) {
            return false;
        }
    }
}
