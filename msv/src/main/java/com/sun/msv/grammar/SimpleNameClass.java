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
