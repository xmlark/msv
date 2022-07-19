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
import com.sun.msv.grammar.IDContextProvider2;
import com.sun.msv.util.DatatypeRef;
import com.sun.msv.util.StartTagInfo;
import com.sun.msv.util.StringRef;
import com.sun.msv.verifier.Acceptor;

/**
 * base implementation of ComplexAcceptor.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ComplexAcceptorBaseImpl extends ContentModelAcceptor
{
    protected final Expression[]    contents;
    
    public ComplexAcceptorBaseImpl(
        REDocumentDeclaration docDecl, Expression combined, Expression[] contents,
        boolean ignoreUndeclaredAttributes ) {
        
        super( docDecl, combined, ignoreUndeclaredAttributes );
        this.contents = contents;
    }

    /** eats string literal */
    public final boolean onText2( String literal, IDContextProvider2 context, StringRef refErr, DatatypeRef refType ) {
        if(!super.onText2(literal,context,refErr,refType))    return false;
        
        final StringToken token = new StringToken(docDecl,literal,context);
        final ResidualCalculator res = docDecl.resCalc;
        
        // some may become invalid, but at least one always remain valid
        for( int i=0; i<contents.length; i++ )
            contents[i] = res.calcResidual( contents[i], token );
        
        return true;
    }
    
    public final boolean stepForward( Acceptor child, StringRef errRef ) {
        if(!super.stepForward(child,errRef))    return false;

        final ResidualCalculator res = docDecl.resCalc;
        Token token;
        
        if( child instanceof SimpleAcceptor ) {
            // this is possible although it is very rare.
            // continuation cannot be used here, because
            // some contents[i] may reject this owner.
            ElementExp cowner = ((SimpleAcceptor)child).owner;
            if( cowner==null )
                // cowner==null means we are currently recovering from an error.
                // so use AnyElementToken to make contents[i] happy.
                token = AnyElementToken.theInstance;
            else
                token = new ElementToken( new ElementExp[]{cowner} );
        } else {
            if( errRef!=null )
                // in error recovery mode
                // pretend that every candidate of child ComplexAcceptor is happy
                token = new ElementToken( ((ComplexAcceptor)child).owners );
            else
                // in normal mode, collect only those satisfied owners.
                token = new ElementToken( ((ComplexAcceptor)child).getSatisfiedOwners() );
        }
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = res.calcResidual( contents[i], token );
        
        return true;
    }
    
    protected boolean onAttribute( AttributeToken token, StringRef refErr ) {
        
        if(!super.onAttribute(token,refErr))    return false;
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = docDecl.attFeeder.feed( contents[i], token, ignoreUndeclaredAttributes );
        
        return true;
    }
    
    public boolean onEndAttributes( StartTagInfo sti, StringRef refErr ) {
        if(!super.onEndAttributes(sti,refErr))    return false;
        
        for( int i=0; i<contents.length; i++ )
            contents[i] = docDecl.attPruner.prune(contents[i]);
        
        return true;
    }
}
