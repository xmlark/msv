/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package com.sun.msv.schmit.reader.xmlschema;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.ElementDeclState;
import com.sun.msv.schmit.grammar.xmlschema.AnnotatedXSElementExp;
import com.sun.msv.schmit.reader.AnnotationParent;
import com.sun.msv.schmit.reader.AnnotationState;
import com.sun.msv.util.StartTagInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SchmitElementDeclState extends ElementDeclState implements AnnotationParent {
    
    /** Parsed annotations. */
    private final List annotations = new ArrayList();

    protected boolean isGrammarElement( StartTagInfo tag ) {
        // this class will handle annotations.
        if(Util.isAnnotationElement(tag))
            return true;
        
        return super.isGrammarElement(tag);
    }

    protected State createChildState(StartTagInfo tag) {
        if(Util.isAnnotationElement(tag))
            // parse it as an annotation
            return new AnnotationState( ((SchmitXMLSchemaReader)reader).dom );
        else
            return super.createChildState(tag);
    }

    public void onEndAnnotation(Node annotation) {
        annotations.add(annotation);
    }

    protected Expression annealDeclaration( ElementDeclExp exp ) {
        exp.setElementExp( new AnnotatedXSElementExp(exp,annotations) );
        return exp;
    }
}
