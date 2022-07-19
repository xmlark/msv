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
