/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and  use in  source and binary  forms, with  or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * - Redistributions  of  source code  must  retain  the above  copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution  in binary  form must  reproduct the  above copyright
 *   notice, this list of conditions  and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * Neither  the  name   of  Sun  Microsystems,  Inc.  or   the  names  of
 * contributors may be  used to endorse or promote  products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS  OR   IMPLIED  CONDITIONS,  REPRESENTATIONS   AND  WARRANTIES,
 * INCLUDING  ANY  IMPLIED WARRANTY  OF  MERCHANTABILITY,  FITNESS FOR  A
 * PARTICULAR PURPOSE  OR NON-INFRINGEMENT, ARE HEREBY  EXCLUDED. SUN AND
 * ITS  LICENSORS SHALL  NOT BE  LIABLE  FOR ANY  DAMAGES OR  LIABILITIES
 * SUFFERED BY LICENSEE  AS A RESULT OF OR  RELATING TO USE, MODIFICATION
 * OR DISTRIBUTION OF  THE SOFTWARE OR ITS DERIVATIVES.  IN NO EVENT WILL
 * SUN OR ITS  LICENSORS BE LIABLE FOR ANY LOST  REVENUE, PROFIT OR DATA,
 * OR  FOR  DIRECT,   INDIRECT,  SPECIAL,  CONSEQUENTIAL,  INCIDENTAL  OR
 * PUNITIVE  DAMAGES, HOWEVER  CAUSED  AND REGARDLESS  OF  THE THEORY  OF
 * LIABILITY, ARISING  OUT OF  THE USE OF  OR INABILITY TO  USE SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
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
