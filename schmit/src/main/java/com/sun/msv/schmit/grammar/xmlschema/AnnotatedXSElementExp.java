/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.grammar.xmlschema;

import java.util.List;

import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.schmit.grammar.AnnotatedPattern;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class AnnotatedXSElementExp extends ElementDeclExp.XSElementExp implements AnnotatedPattern {
    
    private final List annotations;
    
    public AnnotatedXSElementExp(ElementDeclExp decl, List _annotations) {                
        super(decl, decl.getElementExp().elementName, decl.getElementExp().contentModel);
        this.annotations = _annotations;
    }

    public List getAnnotations() {
        return annotations;
    }
}
