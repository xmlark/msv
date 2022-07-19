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

package com.sun.msv.relaxns.verifier;

import org.iso_relax.dispatcher.ElementDecl;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * Acceptor that is used to validate root node of the island.
 * 
 * This object receives {@link DeclImpl}s and validates them.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RulesAcceptor
    extends com.sun.msv.verifier.regexp.ComplexAcceptorBaseImpl {
    
    protected final DeclImpl[]        owners;
    
    /** helper function for constructor */
    private static Expression createCombined( ExpressionPool pool, DeclImpl[] rules ) {
        Expression exp = Expression.nullSet;
        for( int i=0; i<rules.length; i++ )
            exp = pool.createChoice( exp, rules[i].exp );
        return exp;
    }
    
    /** helper function for constructor */
    private static Expression[] getContents( DeclImpl[] rules ) {
        Expression[] r = new Expression[rules.length];
        for( int i=0; i<rules.length; i++ )
            r[i] = rules[i].exp;
        return r;
    }
    
    public RulesAcceptor( REDocumentDeclaration docDecl, DeclImpl[] rules ) {
        this( docDecl, createCombined(docDecl.pool,rules), getContents(rules), rules );
    }
    
    private RulesAcceptor( REDocumentDeclaration docDecl,
        Expression combined, Expression[] contentModels, DeclImpl[] owners ) {
    
        // RulesAcceptor always has ElementExp as the content model,
        // and RulesAcceptor by itself will never contain AttributeExps.
        // so "ignoreUndeclaredAttributes" is meaningless and unused.
        // therefore, just set false.
        super( docDecl, combined, contentModels, false );
        this.owners = owners;
    }
    public Acceptor createClone() {
        Expression[] models = new Expression[contents.length];
        System.arraycopy(contents,0, models, 0, contents.length );
        return new RulesAcceptor( docDecl, getExpression(), models, owners );
    }
    
    /**
     * collects satisfied ElementDeclImpls.
     * 
     * @see com.sun.msv.verifier.regexp.ComplexAcceptor#getSatisfiedOwners()
     */
    ElementDecl[] getSatisfiedElementDecls() {
        int cnt=0;
        for( int i=0; i<owners.length; i++ )
            if( contents[i].isEpsilonReducible() )
                cnt++;
        
        ElementDecl[] r = new DeclImpl[cnt];
        cnt=0;
        for( int i=0; i<owners.length; i++ )
            if( contents[i].isEpsilonReducible() )
                r[cnt++] = owners[i];
        
        return r;
    }
}
