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
import com.sun.msv.grammar.trex.TREXGrammar;

/**
 * parses &lt;grammar&gt; element.
 * 
 * this state is used to parse top-level grammars and nested grammars.
 * grammars merged by include element are handled by MergeGrammarState.
 * 
 * <p>
 * this class provides a new TREXGrammar object to localize names defined
 * within this grammar.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class GrammarState extends DivInGrammarState {
    protected TREXGrammar previousGrammar;
    protected TREXGrammar newGrammar;
    
    protected Expression makeExpression() {
        // start pattern is the grammar-as-a-pattern.
        return newGrammar;
    }

    protected void startSelf() {
        super.startSelf();
        
        previousGrammar = getReader().grammar;
        newGrammar = getReader().sfactory.createGrammar( reader.pool, previousGrammar );
        getReader().grammar = newGrammar;
    }

    public void endSelf() {
        final TREXGrammar grammar = getReader().grammar;
        
        // detect references to undefined pattterns
        reader.detectUndefinedOnes(
            grammar.namedPatterns, TREXBaseReader.ERR_UNDEFINED_PATTERN );

        // is start pattern defined?
        if( grammar.exp==null ) {
            reader.reportError( TREXBaseReader.ERR_MISSING_TOPLEVEL );
            grammar.exp = Expression.nullSet;    // recover by assuming a valid pattern
        }
        
        // this method is called when this State is about to be removed.
        // restore the previous grammar
        if( previousGrammar!=null )
            getReader().grammar = previousGrammar;
        
        // if the previous grammar is null, it means this grammar is the top-level
        // grammar. In that case, leave it there so that GrammarReader can access
        // the loaded grammar.
            
        super.endSelf();
    }
}
