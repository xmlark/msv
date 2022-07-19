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
import com.sun.msv.reader.SimpleState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses the root state of a grammar included as a pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RootIncludedPatternState extends SimpleState implements ExpressionOwner {

    protected State createChildState( StartTagInfo tag ) {
        // grammar has to be treated separately so as not to
        // create unnecessary TREXGrammar object.
//        if(tag.localName.equals("grammar"))
//            return new GrammarState();
        
        State s = reader.createExpressionChildState(this,tag);
//        if(s!=null) {
//            // other pattern element is specified.
//            // create wrapper grammar
//            final TREXBaseReader reader = (TREXBaseReader)this.reader;
//            reader.grammar = new TREXGrammar( reader.pool, null );
//            simple = true;
//        }
        
        return s;
    }
    
    
    /**
     * parsed external pattern will be reported to this object.
     * This state parses top-level, so parentState is null.
     */
    private final IncludePatternState grandParent;
    
    protected RootIncludedPatternState( IncludePatternState grandpa ) {
        this.grandParent = grandpa;
    }
        
    public void onEndChild(Expression exp) {
        if( grandParent!=null )
            // this must be from grammar element. pass it to the IncludePatternState.
            grandParent.onEndChild(exp);

    }
}
