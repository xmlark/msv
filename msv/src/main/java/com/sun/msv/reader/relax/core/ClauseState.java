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

package com.sun.msv.reader.relax.core;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * common part of &lt;tag&gt; and &lt;attPool&gt;.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
abstract class ClauseState extends SimpleState implements ExpressionOwner
{
    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("ref"))            return getReader().getStateFactory().refRole(this,tag);
        if(tag.localName.equals("attribute"))    return getReader().getStateFactory().attribute(this,tag);
        
        return null;    // unrecognized
    }
    
    protected Expression initialExpression()    { return Expression.epsilon; }
    
    protected Expression castExpression( Expression exp, Expression child ) {
        // attributes and references are combined in one sequence
        return reader.pool.createSequence(exp,child);
    }
    

    /** gets reader in type-safe fashion */
    protected RELAXCoreReader getReader() { return (RELAXCoreReader)reader; }



    /**
     * expression object that is being created.
     * See {@link #castPattern} and {@link #annealPattern} methods
     * for how will a pattern be created.
     */
    protected Expression exp = initialExpression();
    
    /** receives a Pattern object that is contained in this element. */
    public final void onEndChild( Expression childExpression ) {
        exp = castExpression( exp, childExpression );
    }
}
