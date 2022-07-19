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

package com.sun.msv.verifier.regexp;

import java.util.Map;

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;

/**
 * this object will be added to Expression.verifierTag
 * to speed up typical validation.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
final class OptimizationTag
{
    /** cached value of string care level.
     * See Acceptor.getStringCareLevel for meanings of value.
     */
    int stringCareLevel = STRING_NOTCOMPUTED;
    
    /** a value indicates that stringCareLevel has not computed yet. */
    public static final int STRING_NOTCOMPUTED = -1;
    
    /**
     * map from element to residual(exp,ElementToken(element))
     * 
     * this map is not applicable when the ElementToken represents
     * more than one element. Because of 'concur' operator.
     * 
     * In RELAX, 
     *  residual(exp,elem1|elem2) = residual(exp,elem1) | residual(exp,elem2)
     * 
     * Since it is possible for multiple threads to access the same OptimizationTag
     * concurrently, it has to be serialized.
     */
    final Map simpleElementTokenResidual = new java.util.Hashtable();
    
    protected static final class OwnerAndCont
    {
        final ElementExp owner;
        final Expression continuation;
        public OwnerAndCont( ElementExp owner, Expression cont )
        { this.owner=owner; this.continuation=cont; }
    };
    /** map from (namespaceURI,tagName) pair to OwnerAndContinuation. */
    final Map transitions = new java.util.Hashtable();

    /** AttributePruner.prune(exp) */
    Expression attributePrunedExpression;
    
//    /** a flag that indicates this expression doesn't have any attribute node.
//     * 
//     * null means unknown.
//     */
//    Boolean isAttributeFree;
}
