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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.trex.ElementPattern;

/**
 * {@link ElementPattern} with annotations.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AnnotatedElementPattern extends ElementPattern implements AnnotatedPattern {
    
    /**
     * Read-only list of {@link org.w3c.dom.Element}s that represents
     * annotation elements attached to this definition.
     */
    private final List annotations;
    
    public AnnotatedElementPattern(NameClass nameClass, Expression contentModel, List _annotations) {
        super(nameClass, contentModel);
        this.annotations = _annotations;
    }
    
    public List getAnnotations() {
        return annotations;
    }
}
