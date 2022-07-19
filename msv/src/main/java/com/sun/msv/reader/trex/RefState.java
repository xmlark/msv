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
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.reader.ExpressionWithoutChildState;
                                                           
/**
 * parses &lt;ref&gt; pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RefState extends ExpressionWithoutChildState {
    
    public RefState( boolean parentRef ) {
        this.parentRef = parentRef;
    }
    
    protected boolean parentRef;
    
    protected Expression makeExpression() {
        final String name = startTag.getCollapsedAttribute("name");
        
        if(name==null) {
            // name attribute is required.
            reader.reportError( TREXBaseReader.ERR_MISSING_ATTRIBUTE,
                "ref","name");
            // recover by returning something that can be interpreted as Pattern
            return Expression.nullSet;
        }
        
        TREXGrammar grammar = ((TREXBaseReader)this.reader).grammar;
        
        if( parentRef ) {
            grammar = grammar.getParentGrammar();
            
            if( grammar==null ) {
                reader.reportError( TREXBaseReader.ERR_NONEXISTENT_PARENT_GRAMMAR );
                return Expression.nullSet;
                // recover by returning something that can be interpreted as Pattern
            }
        }
        
        ReferenceExp r = grammar.namedPatterns.getOrCreate(name);
        wrapUp(r);
        return r;
    }
    
    /**
     * Performs the final wrap-up.
     */
    protected void wrapUp( ReferenceExp r ) {
        reader.backwardReference.memorizeLink(r);
    }

}
