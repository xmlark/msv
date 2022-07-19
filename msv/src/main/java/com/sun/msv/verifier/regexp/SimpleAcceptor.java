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

import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.verifier.Acceptor;

/**
 * Acceptor that will be used when only one ElementExp matches
 * the start tag.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleAcceptor extends ContentModelAcceptor {
    
    /**
     * the expression that should be used by the parent acceptor
     * once if this acceptor is satisfied.
     * 
     * This field can be null. In that case, the continuation has to be computed.
     */
    public final Expression continuation;
    
    /**
     * ElementExp that accepted the start tag.
     * 
     * This acceptor is verifying the content model of this ElementExp.
     * This value is usually non-null, but can be null when Verifier is
     * recovering from eariler errors.
     * null owner means this acceptor is "synthesized" just for proper error recovery,
     * therefor there is no owner element expression.
     */
    public final ElementExp owner;

    public final Object getOwnerType()    { return owner; }

    public SimpleAcceptor(
        REDocumentDeclaration docDecl,
        Expression combined,
        ElementExp owner,
        Expression continuation )
    {
        super(docDecl,combined,
            // ignore undeclared attributes if we are recovering from errors.
            (owner==null)?true:owner.ignoreUndeclaredAttributes);
        this.continuation    = continuation;
        this.owner            = owner;
    }
    
    public Acceptor createClone() {
        return new SimpleAcceptor( docDecl, getExpression(), owner, continuation );
    }
}
