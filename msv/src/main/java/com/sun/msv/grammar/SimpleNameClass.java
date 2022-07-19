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

package com.sun.msv.grammar;

import com.sun.msv.util.StringPair;

/**
 * a NameClass that accepts only one fixed name.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class SimpleNameClass extends NameClass {
    public final String    namespaceURI;
    public final String localName;
    
    public boolean accepts( String namespaceURI, String localName ) {
        // wild cards are treated as symbols, rather than strings.
        return    ( this.namespaceURI.equals(namespaceURI) || NAMESPACE_WILDCARD==namespaceURI )
            &&  ( this.localName.equals(localName) || LOCALNAME_WILDCARD==localName );
    }
    
    public Object visit( NameClassVisitor visitor ) { return visitor.onSimple(this); }

    public SimpleNameClass( StringPair name ) {
        this( name.namespaceURI, name.localName );
    }
    
    public SimpleNameClass( String namespaceURI, String localName ) {
        this.namespaceURI    = namespaceURI;
        this.localName        = localName;
    }
    
    public StringPair toStringPair() {
        return new StringPair(namespaceURI,localName);
    }
    
    public String toString() {
        if( namespaceURI.length()==0 )    return localName;
        else                            return /*namespaceURI+":"+*/localName;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
