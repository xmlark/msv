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

package com.sun.msv.relaxns.grammar.trex;

import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;

import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.TREXGrammar;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;

/**
 * IslandSchema implementation for TREX pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TREXIslandSchema extends IslandSchemaImpl
{
    /** underlying TREX pattern which this IslandSchema is representing */
    protected final TREXGrammar grammar;
    
    public TREXIslandSchema( TREXGrammar grammar ) {
        this.grammar = grammar;
        
        // export all named patterns.
        // TODO: modify to export only those element declarations.
        ReferenceExp[] refs = grammar.namedPatterns.getAll();
        for( int i=0; i<refs.length; i++ )
            elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
    }
    
    protected Grammar getGrammar() {
        return grammar;
    }
    
    public void bind( SchemaProvider provider, ErrorHandler handler ) {
        Binder binder = new Binder( provider, handler, grammar.pool );
        bind( grammar.namedPatterns, binder );
    }
}
