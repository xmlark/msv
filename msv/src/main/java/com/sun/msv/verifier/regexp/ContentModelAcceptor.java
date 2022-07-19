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
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;

/**
 * base implementation for SimpleAcceptor and ComplexAcceptor
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ContentModelAcceptor extends ExpressionAcceptor {
    
    protected ContentModelAcceptor(
        REDocumentDeclaration docDecl, Expression exp,
        boolean ignoreUndeclaredAttributes ) {
    
        super(docDecl,exp,ignoreUndeclaredAttributes);
    }
    
    public boolean stepForward( Acceptor child, StringRef errRef ) {
        // TODO: explicitly mention that where the error recovery should be done.
        if( child instanceof SimpleAcceptor ) {
            SimpleAcceptor sa = (SimpleAcceptor)child;
            if(sa.continuation!=null)
                // if the continuation is available,
                // the stepForward will be very fast.
                return stepForwardByContinuation( sa.continuation, errRef );
            else
                // otherwise we have to compute the residual.
                return stepForward( new ElementToken(new ElementExp[]{sa.owner}), errRef );
        }
        if( child instanceof ComplexAcceptor ) {
            ComplexAcceptor ca = (ComplexAcceptor)child;
            return stepForward(
                new ElementToken(
                    (errRef!=null)?
                        ca.owners:    // in error recovery mode, pretend that every owner is happy.
                        ca.getSatisfiedOwners() ),
                errRef);
        }
        throw new Error();    // child must be either Simple or Complex.
    }
    
    /**
     * creates actual Acceptor object from the computed result.
     */
    protected Acceptor createAcceptor(
        Expression combined, Expression continuation,
        ElementExp[] primitives, int numPrimitives ) {
        
        if( primitives==null || numPrimitives<=1 ) {
            // primitives==null is possible when recovering from error.
            
            // in this special case, combined child pattern and primitive patterns are the same.
            // therefore we don't need to keep track of primitive patterns.
            return new SimpleAcceptor(
                docDecl, combined,
                (primitives==null)?null:primitives[0],
                continuation );
        }

        // TODO: implements MultipleAcceptor for cases that
        // combined expression is unnecessary but there are more than one primitive.
        
        if( com.sun.msv.driver.textui.Debug.debug )
            System.out.println("ComplexAcceptor is used");
        
        // we need a fresh array.
        ElementExp[] owners = new ElementExp[numPrimitives];
        System.arraycopy( primitives, 0, owners, 0, numPrimitives );
        
        return new ComplexAcceptor( docDecl, combined, owners );
    }
    
    // ContentModelAcceptor does not support type-assignment.
    // This will be supported by SimpleAcceptor only.
    public Object getOwnerType() { return null; }
}
