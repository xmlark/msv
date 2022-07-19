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

package com.sun.msv.reader.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class DefineState extends SimpleState implements ExpressionOwner {

    /**
     * expression object that is being created.
     * See {@link #castPattern} and {@link #annealPattern} methods
     * for how will a pattern be created.
     */
    protected Expression exp = null; // or Expression.epsilon if <define /> is allowed.
    
    /**
     * Container to which the expression will be assigned.
     */
    private ReferenceExp ref;
    
    /**
     * Obtains a {@link ReferenceExp} that represents the target
     * pattern block.
     * 
     * @return null
     *      If there was an error in the input, this method may
     *      return null.
     */
    public final ReferenceExp getRef() {
        return ref;
    }
    /** receives a Pattern object that is contained in this element. */
    public final void onEndChild( Expression child ) {
        if( exp==null ) { // first one.
            exp = child;
        } else {
            exp = reader.pool.createSequence(exp,child);
        }
    }
    
    protected void startSelf() {
        super.startSelf();
        ref = getReference();
    }

    
    protected void endSelf() {
        if( exp==null ) {
            reader.reportError( GrammarReader.ERR_MISSING_CHILD_EXPRESSION );
            exp = Expression.nullSet;
            // recover by assuming some pattern.
        }

        if(ref==null)    return;    // error. abort.
        
        final TREXBaseReader reader = (TREXBaseReader)this.reader;
        final String combine = startTag.getCollapsedAttribute("combine");
        
        exp = callInterceptExpression(exp);
        
        // combine two patterns
        Expression newexp = doCombine( ref, exp, combine );
        if( newexp==null )
            reader.reportError( TREXBaseReader.ERR_BAD_COMBINE, combine );
            // recover by ignoring this definition
        else
            ref.exp = newexp;
    
        reader.setDeclaredLocationOf(ref);

        ((ExpressionOwner)parentState).onEndChild(ref);
    }

    
    protected State createChildState( StartTagInfo tag ) {
        return reader.createExpressionChildState(this,tag);
    }

    /**
     * 
     * @return null in case of error.
     */
    protected ReferenceExp getReference() {
        final String name = startTag.getCollapsedAttribute("name");
        
        if(name==null) {
            // name attribute is required.
            reader.reportError( TREXBaseReader.ERR_MISSING_ATTRIBUTE,
                "ref","name");
            return null;
        }
        
        final TREXBaseReader reader = (TREXBaseReader)this.reader;
        return reader.grammar.namedPatterns.getOrCreate(name);
    }
    
    
    /**
     * combines two expressions into one as specified by the combine parameter,
     * and returns a new expression.
     * 
     * If the combine parameter is invalid, then return null.
     */
    protected abstract Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine );
}
