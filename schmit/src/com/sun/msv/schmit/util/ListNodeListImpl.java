/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.util;

import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * {@link NodeList} implementation by using {@link List}
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public final class ListNodeListImpl implements NodeList {
    
    private final List nodes;
    
    public ListNodeListImpl(List _nodes) {
        this.nodes = _nodes;
    }
    
    public Node item(int index) {
        return (Node)nodes.get(index);
    }

    public int getLength() {
        return nodes.size();
    }
}
