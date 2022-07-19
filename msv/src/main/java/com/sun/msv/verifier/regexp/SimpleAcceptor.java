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
