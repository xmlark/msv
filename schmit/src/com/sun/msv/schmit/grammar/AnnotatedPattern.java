/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.grammar;

import java.util.List;

/**
 * Pattern with annotation.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public interface AnnotatedPattern {
    /**
     * Returns associations attached to this pattern.
     * 
     * @return
     * a list of DOM Elements.
     */
    List getAnnotations();
}
