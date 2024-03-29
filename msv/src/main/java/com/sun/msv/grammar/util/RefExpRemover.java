/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.grammar.util;

import java.util.Set;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * removes all ReferenceExp from AGM.
 * 
 * when named expression is nullSet, it cannot be used.
 * by replacing ReferenceExp by its definition, those unavailable expressions
 * will be properly removed from AGM.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefExpRemover extends ExpressionCloner {

    /** set of visited ElementExps */
    private final Set<ElementExp> visitedElements = new java.util.HashSet<ElementExp>();

    private final boolean recursive;

    /**
     * @param _recursive
     *        <p>
     *        If true, this object behaves destructively. It recursively
     *        visits all the reachable expressions and removes ReferenceExps.
     *        In this process, this object changes the content model of 
     *        ElementExps.
     *        
     *        <p>
     *        If false, this object doesn't visit the content models of child
     *        elements, therefore, it behaves non-destructively. Nothing in the
     *        original expression will be touched.
     */
    public RefExpRemover(ExpressionPool pool, boolean _recursive) {
        super(pool);
        this.recursive = _recursive;
    }

    public Expression onElement(ElementExp exp) {
        if (!recursive)
            // do not touch child elements.
            return exp;

        if (!visitedElements.contains(exp)) {
            // remove refs from this content model
            visitedElements.add(exp);
            exp.contentModel = exp.contentModel.visit(this);
        }
        if (exp.contentModel == Expression.nullSet)
            return Expression.nullSet; // this element is not allowed
        else
            return exp;
    }

    public Expression onAttribute(AttributeExp exp) {
        Expression content = exp.exp.visit(this);
        if (content == Expression.nullSet)
            return Expression.nullSet; // this attribute is not allowed
        else
            return pool.createAttribute(exp.nameClass, content, exp.getDefaultValue());
    }
    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }
    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }
}
