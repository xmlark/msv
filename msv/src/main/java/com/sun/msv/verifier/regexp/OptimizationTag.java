/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
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
