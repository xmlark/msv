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
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.verifier.Acceptor;

/**
 * Accept that is used when more than one pattern can be applicable to the current context.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ComplexAcceptor extends ComplexAcceptorBaseImpl {
    
    /**
     * each item of this array should be considered as read-only.
     */
    public final ElementExp[]    owners;
    
    private static Expression[] createDefaultContentModels( ElementExp[] owners, ExpressionPool pool ) {
        Expression[] r = new Expression[owners.length];
        for( int i=0; i<owners.length; i++ )
            r[i] = owners[i].contentModel.getExpandedExp(pool);
        return r;
    }
    
    public ComplexAcceptor( REDocumentDeclaration docDecl,
            Expression combined, ElementExp[] primitives ) {
        this( docDecl, combined,
            createDefaultContentModels(primitives,docDecl.pool), primitives );
    }
    
    public ComplexAcceptor(
        REDocumentDeclaration docDecl, Expression combined,
        Expression[] contentModels,    ElementExp[] owners ) {
        
        // since all owners should belong to the same schema language,
        // ignoreUndeclaredAttributes must be the same.
        // that's why I'm using owners[0].
        super( docDecl, combined, contentModels, owners[0].ignoreUndeclaredAttributes );
        this.owners = owners;
    }

    public Acceptor createClone() {
        Expression[] models = new Expression[contents.length];
        System.arraycopy(contents,0, models, 0, contents.length );
        return new ComplexAcceptor( docDecl, getExpression(), models, owners );
    }
    
    /**
     * collects satisfied ElementExps.
     * 
     * "satisfied ElementExps" are ElementExps whose
     * contents is now epsilon reducible.
     */
    public final ElementExp[] getSatisfiedOwners()
    {
        ElementExp[] satisfied;
        
        int i,cnt;
        // count # of satisfied ElementExp.
        for( i=0,cnt=0; i<contents.length; i++ )
            if( contents[i].isEpsilonReducible() )    cnt++;
            
        if(cnt==0)    return new ElementExp[0];    // no one is satisfied.
            
        satisfied = new ElementExp[cnt];
        for( i=0,cnt=0; i<contents.length; i++ )
            if( contents[i].isEpsilonReducible() )
                satisfied[cnt++] = owners[i];
        
        return satisfied;
    }
}
