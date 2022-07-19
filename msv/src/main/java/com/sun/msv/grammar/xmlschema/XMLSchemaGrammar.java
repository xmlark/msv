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
