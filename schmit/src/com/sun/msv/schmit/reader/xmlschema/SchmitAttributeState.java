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
import com.sun.msv.grammar.NameClass;
import com.sun.msv.reader.State;
import com.sun.msv.reader.xmlschema.AttributeState;
import com.sun.msv.schmit.grammar.relaxng.AnnotatedAttributePattern;
import com.sun.msv.schmit.reader.AnnotationParent;
import com.sun.msv.schmit.reader.AnnotationState;
import com.sun.msv.util.StartTagInfo;

/**
 * 
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public class SchmitAttributeState extends AttributeState implements AnnotationParent {
   
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

    protected Expression createAttribute(NameClass nc, Expression exp) {
        return new AnnotatedAttributePattern( nc, exp, annotations );
    }

}
