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

package com.sun.msv.grammar.util;

import java.util.Set;

import com.sun.msv.grammar.AnyNameClass;
import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NameClassVisitor;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.util.StringPair;

/**
 * computes the possible names.
 * 
 * <p>
 * See <a href="http://lists.oasis-open.org/ob/htsearch?config=lists_oasis-open_org&restrict=relax-ng%2F&method=and&sort=score&words=possibleNames">
 * the description</a>.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class PossibleNamesCollector implements NameClassVisitor {
    
    /**
     * computes all possibile names for this name class, and returns
     * the set of {@link StringPair}.
     */
    public static Set calc( NameClass nc ) {
        PossibleNamesCollector col = new PossibleNamesCollector();
        nc.visit(col);
        return col.names;
    }
    
    
    public static final String MAGIC = "\u0000";
    private static final StringPair pairForAny = new StringPair( MAGIC, MAGIC );
    
    /** this set will receive all possible names. */
    private Set names = new java.util.HashSet();
    
    public Object onChoice( ChoiceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
    public Object onAnyName( AnyNameClass nc ) {
        names.add( pairForAny );
        return null;
    }
    public Object onSimple( SimpleNameClass nc ) {
        names.add( new StringPair( nc.namespaceURI, nc.localName ) );
        return null;
    }
    public Object onNsName( NamespaceNameClass nc ) {
        names.add( new StringPair( nc.namespaceURI, MAGIC ) );
        return null;
    }
    public Object onNot( NotNameClass nc ) {
        names.add( pairForAny );
        nc.child.visit(this);
        return null;
    }
    public Object onDifference( DifferenceNameClass nc ) {
        nc.nc1.visit(this);
        nc.nc2.visit(this);
        return null;
    }
};
