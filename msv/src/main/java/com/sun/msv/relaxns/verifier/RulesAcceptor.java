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
