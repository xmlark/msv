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

package com.sun.msv.grammar;

import java.util.Iterator;
import java.util.Map;

/**
 * Container of ReferenceExp. a map from name to ReferenceExp.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ReferenceContainer implements java.io.Serializable {
    
    protected final Map impl = new java.util.HashMap();
    
    /**
     * gets or creates ReferenceExp object.
     * 
     * Derived class should provide type-safe accesser methods.
     * 
     * Usually, this method is only necessary for some kind of grammar loader.
     * If you are programming an application over MSV,
     * {@link #_get(String)} method is probably what you need.
     */
    public final ReferenceExp _getOrCreate( String name ) {
        Object o = impl.get(name);
        if(o!=null)    return (ReferenceExp)o;
        
        // this is the first time this name is used.
        // so create a ReferenceExp here.
        ReferenceExp exp = createReference(name);
        impl.put(name,exp);
        return exp;
    }
    
    /** creates a new reference object with given name */
    protected abstract ReferenceExp createReference( String name );

    /**
     * replaces the current ReferenceExp by newly specified reference exp.
     */
    public void redefine( String name, ReferenceExp newExp ) {
        if( impl.put(name,newExp)==null )
            // no object is associated with this name.
            throw new IllegalArgumentException();
    }
    
    /** gets a referenced expression
     * 
     * Derived class should provide type-safe accesser methods.
     * 
     * @return null
     *        if no expression is defined with the given name.
     */
    public final ReferenceExp _get( String name ) {
        Object o = impl.get(name);
        if(o!=null)    return (ReferenceExp)o;
        else        return null;    // not found    
    }
    
    /** iterates all ReferenceExp in this container */
    public final Iterator iterator() {
        return impl.values().iterator();
    }
    
    /** obtains all items in this container. */
    public final ReferenceExp[] getAll() {
        ReferenceExp[] r = new ReferenceExp[size()];
        impl.values().toArray(r);
        return r;
    }
    
    /** removes an object from this container.
     * 
     * @return
     *    removed object. Null if no such name is found.
     */
    public final ReferenceExp remove( String name ) {
        return (ReferenceExp)impl.remove(name);
    }
    
    /** gets the number of ReferenceExps in this container. */
    public final int size()    {
        return impl.size();
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
