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

import com.sun.msv.grammar.util.NameClassCollisionChecker;
import com.sun.msv.grammar.util.NameClassComparator;
import com.sun.msv.grammar.util.NameClassSimplifier;
import com.sun.msv.util.StringPair;

/**
 * validator of (namespaceURI,localPart) pair.
 * 
 * This is equivalent to RELAX NG's "name class".
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClass implements java.io.Serializable {
    /**
     * checks if this name class accepts given namespace:localName pair.
     * 
     * @param namespaceURI
     *        namespace URI to be tested. If this value equals to
     *        NAMESPACE_WILDCARD, implementation must assume that
     *        valid namespace is specified. this twist will be used for
     *        error diagnosis.
     * 
     * @param localName
     *        local part to be tested. As with namespaceURI, LOCALNAME_WILDCARD
     *        will acts as a wild card.
     * 
     * @return
     *        true if the pair is accepted,
     *        false otherwise.
     */
    public abstract boolean accepts( String namespaceURI, String localName );

    public final boolean accepts( StringPair name ) {
        return accepts( name.namespaceURI, name.localName );
    }
    
    /** Returns true if this name class is a superset of another name class. */
    public final boolean includes( NameClass rhs ) {
        boolean r = new NameClassComparator() {
            protected void probe(String uri, String local) {
                if( !nc1.accepts(uri,local) && nc2.accepts(uri,local) )
                    throw eureka;   // this is not a super-set!
            }
        }.check(this,rhs);
        
        return !r;
    }
    
    /** Returns true if this name class doesn't accept anything. */
    public boolean isNull() {
        return !new NameClassCollisionChecker().check(this,NameClass.ALL);
    }
    
    /**
     * Returns true if this name class represents the same set as the given name class.
     */
    public final boolean isEqualTo( NameClass rhs ) {
        boolean r = new NameClassComparator() {
            protected void probe(String uri, String local) {
                boolean a = nc1.accepts(uri,local);
                boolean b = nc2.accepts(uri,local);
                
                if( (a&&!b) || (!a&&b) )    throw eureka;
            }
        }.check(this,rhs);
        
        return !r;
    }

    /**
     * Computes the equivalent but simple name class.
     */
    public NameClass simplify() {
        return NameClassSimplifier.simplify(this);
    }
    
    
    /**
     * visitor pattern support
     */
    public abstract Object visit( NameClassVisitor visitor );
    
    /** wildcard should be accepted by any name class. */
    public static final String NAMESPACE_WILDCARD = "*";
    public static final String LOCALNAME_WILDCARD = "*";
    
    
    /** Computes the intersection of two name classes. */
    public static NameClass intersection( NameClass lhs, NameClass rhs ) {
        return NameClassSimplifier.simplify(
            new DifferenceNameClass( lhs, new NotNameClass(rhs) ) );
    }

    /** Computes the union of two name classes. */
    public static NameClass union( NameClass lhs, NameClass rhs ) {
        return NameClassSimplifier.simplify(
            new ChoiceNameClass(lhs,rhs) );
    }
    
    /** name class that accepts everything. */
    public static final NameClass ALL = new AnyNameClass();
    
    /** Name class that accepts nothing. */
    public static final NameClass NONE = new NotNameClass(ALL);

    
    // serialization support
    private static final long serialVersionUID = 1;    
}
