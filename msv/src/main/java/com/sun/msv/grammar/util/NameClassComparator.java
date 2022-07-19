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

package com.sun.msv.grammar.util;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;

/**
 * Abstract implementation of a function
 * <pre>
 *   NameClass x NameClass ->  boolean
 * </pre>
 * 
 * Override the probe method to define the actual function.
 *
 * 
 * <p>
 * To compute, create an instance and call the check method. This class is not
 * reentrant, so the caller is responsible not to reuse the same object by multiple
 * threads.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class NameClassComparator implements NameClassVisitor {
            
    /** Two name classes to be tested. */
    protected NameClass nc1,nc2;
            
    /**
     * This exception will be thrown when a collision is found.
     */
    protected final RuntimeException eureka = new RuntimeException();
            
    /**
     * Returns true if two name classes collide.
     */
    public boolean check( NameClass _new, NameClass _old ) {
                
        try {
            nc1 = _new;
            nc2 = _old;
            _old.visit(this);
            _new.visit(this);
            return false;
        } catch( RuntimeException e ) {
            if(e==eureka)   return true;    // the collision was found.
            throw e;
        }
    }
            
    /**
     * Throw <code>eureka</code> to return true from the probe method.
     */
    protected abstract void probe( String uri, String local );
            
    private /*static*/ final String MAGIC = "\u0000";
            
    public Object onAnyName( AnyNameClass nc ) {
        probe(MAGIC,MAGIC);
        return null;
    }
    public Object onNsName( NamespaceNameClass nc ) {
        probe(nc.namespaceURI,MAGIC);
        return null;
    }
    public Object onSimple( SimpleNameClass nc ) {
        probe(nc.namespaceURI,nc.localName);
        return null;
    }
    public Object onNot( NotNameClass nc ) {
        nc.child.visit(this);
        return null;
    }
    public Object onDifference( DifferenceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
    public Object onChoice( ChoiceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
}