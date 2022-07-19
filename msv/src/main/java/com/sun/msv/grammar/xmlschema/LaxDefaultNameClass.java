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

import java.util.Set;

import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * Special name class implementation used for the wild card of the "lax" mode.
 * 
 * <p>
 * In "lax" mode, we need a name class that matches all undefined names.
 * Although it is possible to use DifferenceNameClass for this purpose,
 * it is not a cost-efficient way because typically it becomes very large.
 * (If there are twenty element declarations, we'll need twenty DifferenceNameClass
 * to exclude all defined names).
 * 
 * <p>
 * This name class uses a {@link Set} to hold multiple names. If a name
 * is contained in that set, it'll be rejected. If a name is not contained,
 * it'll be accepted.
 * 
 * <p>
 * Special care is taken to make this NC as seamless as possible.
 * When the visit method is called, the equivalent name class is constructed
 * internally and the visitor will visit that name class. In this way, the visitors
 * won't notice the existance of this "special" name class.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class LaxDefaultNameClass extends NameClass {
    
    /**
     * @param _base
     *        this name class accepts a name if
     *        <ol>
     *         <li>it's in the 'base" name class and
     *         <li>it's not one of those excluded names
     */
    public LaxDefaultNameClass( NameClass _base ) {
        this.base = _base;
        names.add( new StringPair(NAMESPACE_WILDCARD,LOCALNAME_WILDCARD) );
    }
    
    private NameClass base;
    
    public Object visit( NameClassVisitor visitor ) {
        // create equivalent name class and let visitor visit it.
        if( equivalentNameClass==null ) {
            NameClass nc = base;
            StringPair[] items = (StringPair[])names.toArray(new StringPair[0]);
            for( int i=0; i<items.length; i++ ) {
                if( items[i].namespaceURI==NAMESPACE_WILDCARD
                 || items[i].localName==LOCALNAME_WILDCARD )
                    continue;
                
                nc = new DifferenceNameClass(nc,
                    new SimpleNameClass(items[i]));
            }
            equivalentNameClass = nc;
        }
        
        return equivalentNameClass.visit(visitor);
    }
    
    /**
     * equivalent name class by conventional primitives.
     * Initially null, and created on demand.
     */
    protected NameClass equivalentNameClass;
    
    public boolean accepts( String namespaceURI, String localName ) {
        return base.accepts(namespaceURI,localName) &&
                !names.contains( new StringPair(namespaceURI,localName) );
    }
    
    /**
     * set of {@link StringPair}s.
     * each item represents one name.
     * it also contains WILDCARD as entry.
     */
    private final Set names = new java.util.HashSet();
    
    /**
     * add a name so that this name will be rejected by the accepts method.
     */
    public void addName( String namespaceURI, String localName ) {
        names.add( new StringPair(namespaceURI,localName) );
        names.add( new StringPair(namespaceURI,LOCALNAME_WILDCARD) );
        names.add( new StringPair(NAMESPACE_WILDCARD,localName) );
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
