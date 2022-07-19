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

package com.sun.msv.grammar.xmlschema;

import java.util.Iterator;
import java.util.Map;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.Grammar;

/**
 * set of XML Schema. This set can be used to validate a document.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class XMLSchemaGrammar implements Grammar {

    public XMLSchemaGrammar() {
        this( new ExpressionPool() );
    }
    
    public XMLSchemaGrammar( ExpressionPool pool ) {
        this.pool = pool;
    }
    
    /** pool object which was used to construct this grammar. */
    protected final ExpressionPool pool;
    public final ExpressionPool getPool() {
        return pool;
    }
    
    public Expression topLevel;
    public final Expression getTopLevel() {
        return topLevel;
    }

    /** map from namespace URI to loaded XMLSchemaSchema object. */
    protected final Map schemata = new java.util.HashMap();
    
    /** gets XMLSchemaSchema object that has the given target namespace.
     * 
     * @return null if no schema is associated with that namespace.
     */
    public XMLSchemaSchema getByNamespace( String targetNamesapce ) {
        return (XMLSchemaSchema)schemata.get(targetNamesapce);
    }
    
    /**
     * returns an Iterator that enumerates XMLSchemaSchema objects
     * that are defined in this grammar.
     */
    public Iterator iterateSchemas() {
        return schemata.values().iterator();
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
