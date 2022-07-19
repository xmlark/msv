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

package com.sun.msv.reader.trex.classic;

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;

/**
 * parses &lt;define&gt; declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DefineState extends com.sun.msv.reader.trex.DefineState {
    
    /**
     * combines two expressions into one as specified by the combine parameter,
     * and returns a new expression.
     * 
     * If the combine parameter is invalid, then return null.
     */
    protected Expression doCombine( ReferenceExp baseExp, Expression newExp, String combine ) {
        
        final TREXGrammarReader reader = (TREXGrammarReader)this.reader;
        
        if( baseExp.exp==null ) {
            // this is the first time definition
            if( combine!=null )
                // "combine" attribute will be ignored
                reader.reportWarning( TREXGrammarReader.WRN_COMBINE_IGNORED, baseExp.name );
            return newExp;
        }

        // some pattern is already defined under this name.
        
        // make sure that the previous definition was in a different file.
        if( reader.getDeclaredLocationOf(baseExp).getSystemId().equals(
                reader.getLocator().getSystemId() ) ) {
            reader.reportError( TREXGrammarReader.ERR_DUPLICATE_DEFINITION, baseExp.name );
            // recovery by ignoring this definition
            return baseExp.exp;
        }
            
        if( combine==null ) {
            // second definition without @combine.
            reader.reportError( new Locator[]{location, reader.getDeclaredLocationOf(baseExp)},
                TREXGrammarReader.ERR_COMBINE_MISSING, new Object[]{baseExp.name} );
            // recover by ignoring this definition
            return baseExp.exp;
        }

        
        
        if( combine.equals("group") )
            return reader.pool.createSequence( baseExp.exp, newExp );
        else
        if( combine.equals("choice") )
            return reader.pool.createChoice( baseExp.exp, newExp );
        else
        if( combine.equals("replace") )
            return exp;
        else
        if( combine.equals("interleave") )
            return reader.pool.createInterleave( baseExp.exp, newExp );
        else
        if( combine.equals("concur") )
            return reader.pool.createConcur( baseExp.exp, newExp );
        else
            return null;
    }
}
