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

import java.util.Set;

import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.util.DatatypeRef;

/**
 * special StringToken that acts as a wild card.
 * 
 * This object is used for error recovery. It collects all TypedStringExps
 * that ate the token.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class StringRecoveryToken extends StringToken {
    
    StringRecoveryToken( StringToken base ) {
        this( base, new java.util.HashSet() );
    }
    
    StringRecoveryToken( StringToken base, Set failedExps ) {
        super( base.resCalc, base.literal, base.context, null );
        this.failedExps = failedExps;
    }
    
    /**
     * TypedStringExps and ListExps that
     * rejected this token are collected into this set.
     */
    final Set failedExps;
    
    public boolean match( DataExp exp ) {
        if( super.match(exp) )
            return true;
        
        // this datatype didn't accept me. so record it for diagnosis.
        failedExps.add( exp );
        return true;
    }
    
    public boolean match( ValueExp exp ) {
        if( super.match(exp) )
            return true;
        
        // this datatype didn't accept me. so record it for diagnosis.
        failedExps.add( exp );
        return true;
    }
    
    public boolean match( ListExp exp ) {
        super.match(exp);
        return true;
    }
        
    protected StringToken createChildStringToken( String literal, DatatypeRef dtRef ) {
        return new StringRecoveryToken(
            new StringToken( resCalc, literal, context, dtRef ) );
    }

}
