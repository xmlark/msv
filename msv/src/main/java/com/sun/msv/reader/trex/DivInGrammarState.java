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

package com.sun.msv.reader.trex;

import com.sun.msv.grammar.Expression;
import com.sun.msv.reader.ExpressionOwner;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * Used to parse merged grammars. Also &lt;div&gt; element in the grammar element
 * (of RELAX NG).
 * 
 * DivInGrammarState itself should not be a ExpressionState. However, GrammarState,
 * which is a derived class of this class, is a ExpressionState.
 * 
 * Therefore this class has to extend ExpressionState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DivInGrammarState extends ExpressionState implements ExpressionOwner {
    
    protected final TREXBaseReader getReader() { return (TREXBaseReader)reader; }
    
    protected Expression makeExpression() {
        // this method doesn't provide any pattern
        return null;
    }

    protected State createChildState( StartTagInfo tag ) {
        if(tag.localName.equals("start"))    return getReader().sfactory.start(this,tag);
        if(tag.localName.equals("define"))    return getReader().sfactory.define(this,tag);
        if(tag.localName.equals("include"))    return getReader().sfactory.includeGrammar(this,tag);
        // div is available only for RELAX NG.
        // The default implementation of divInGrammar returns null.
        if(tag.localName.equals("div"))        return getReader().sfactory.divInGrammar(this,tag);
        return null;
    }
    
    // DefineState and StartState is implemented by using ExpressionState.
    // By contract of that interface, this object has to implement ExpressionOwner.
    public void onEndChild( Expression exp ) {}    // do nothing.
}
