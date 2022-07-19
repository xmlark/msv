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

package com.sun.msv.reader.trex.ng.comp;

import org.xml.sax.Locator;

import com.sun.msv.grammar.relaxng.RELAXNGGrammar;

abstract class CompatibilityChecker {
    
    
    protected final RELAXNGCompReader reader;
    protected final RELAXNGGrammar grammar;
    
    protected CompatibilityChecker( RELAXNGCompReader _reader ) {
        this.reader = _reader;
        this.grammar = (RELAXNGGrammar)_reader.getGrammar();
    }
    
    protected abstract void setCompatibility( boolean val );
    
    /**
     * reports the compatibility related error.
     * 
     * <p>
     * Since the processor is required to validate a schema even if 
     * it's not compatible with some of the features, we cannot report
     * those errors as real "errors".
     */
    protected void reportCompError( Locator[] locs, String propertyName ) {
        // TODO: it maybe useful to implement a switch
        // that makes those warnings as errors.
        reportCompError(locs,propertyName,null);
    }
    protected void reportCompError( Locator[] locs, String propertyName, Object[] args ) {
        setCompatibility(false);
        reader.reportWarning(propertyName,args,locs);
    }
}
