/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.grammar.relaxng;

import java.util.List;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.schmit.grammar.AnnotatedPattern;

/**
 * {@link AttributeExp} with annotations.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AnnotatedAttributePattern extends AttributeExp implements AnnotatedPattern {
    
    /**
     * Read-only list of {@link org.w3c.dom.Element}s that represents
     * annotation elements attached to this definition.
     */
    private final List annotations;
    
    public AnnotatedAttributePattern(NameClass nameClass, Expression exp, List _annotations) {
        super(nameClass, exp);
        this.annotations = _annotations;
    }

    public List getAnnotations() {
        return annotations;
    }

}
