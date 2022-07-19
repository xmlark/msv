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

package com.sun.msv.relaxns.grammar.trex;

import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;

/**
 * IslandSchema implementation for TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXIslandSchema extends IslandSchemaImpl
{
    /** underlying TREX pattern which this IslandSchema is representing */
    protected final TREXGrammar grammar;
    
    public TREXIslandSchema( TREXGrammar grammar ) {
        this.grammar = grammar;
        
        // export all named patterns.
        // TODO: modify to export only those element declarations.
        ReferenceExp[] refs = grammar.namedPatterns.getAll();
        for( int i=0; i<refs.length; i++ )
            elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
    }
    
    protected Grammar getGrammar() {
        return grammar;
    }
    
    public void bind( SchemaProvider provider, ErrorHandler handler ) {
        Binder binder = new Binder( provider, handler, grammar.pool );
        bind( grammar.namedPatterns, binder );
    }
}
