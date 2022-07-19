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

package com.sun.msv.relaxns.grammar.relax;

import java.util.Iterator;
import java.util.Set;

import org.iso_relax.dispatcher.ElementDecl;
import org.iso_relax.dispatcher.IslandSchema;
import org.iso_relax.dispatcher.SchemaProvider;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRules;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.relaxns.grammar.DeclImpl;
import com.sun.msv.relaxns.grammar.ExternalElementExp;
import com.sun.msv.relaxns.verifier.IslandSchemaImpl;

/**
 * IslandSchema implementation for RELXA module.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class RELAXIslandSchema extends IslandSchemaImpl
{
    /** underlying RELAX module which this IslandSchema is representing */
    protected final RELAXModule module;

    protected Set pendingAnyOtherElements;

    public RELAXIslandSchema( RELAXModule module, Set pendingAnyOtherElements ) {
        this.module = module;
        this.pendingAnyOtherElements = pendingAnyOtherElements;
        
        // export elementRules as ElementDecl
        ReferenceExp[] refs= module.elementRules.getAll();
        for( int i=0; i<refs.length; i++ )
            if( ((ElementRules)refs[i]).exported )
                elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
        
        // export hedgeRules as ElementDecl.
        // each exportable hedgeRule must be of length 1,
        // but it should have already checked.
        refs = module.hedgeRules.getAll();
        for( int i=0; i<refs.length; i++ )
            if ( ((HedgeRules)refs[i]).exported )
                    elementDecls.put( refs[i].name, new DeclImpl(refs[i]) );
        
        // export attPools as AttributesDecl
        ExportedAttPoolGenerator expGen = new ExportedAttPoolGenerator( module.pool );
        refs = module.attPools.getAll();
        for( int i=0; i<refs.length; i++ )
            if( ((AttPoolClause)refs[i]).exported )
                attributesDecls.put( refs[i].name,
                    new DeclImpl( refs[i].name, expGen.create(module,refs[i].exp) ) );
    }
    
    protected Grammar getGrammar() {
        return module;
    }
    
    public void bind( SchemaProvider provider, ErrorHandler handler ) throws SAXException {
        {// wrap up anyOtherElements.
            Expression pseudoContentModel = createChoiceOfAllExportedRules(provider);
                
            Iterator itr = pendingAnyOtherElements.iterator();
            while( itr.hasNext() )
                ((AnyOtherElementExp)itr.next()).wrapUp(module,pseudoContentModel,provider,handler);
            pendingAnyOtherElements = null;
        }
        
        Binder binder = new Binder( provider, handler, module.pool );
        bind( module.elementRules, binder );
        bind( module.hedgeRules, binder );
        bind( module.attPools, binder );
        bind( module.tags, binder );
    }
    
    /**
     * creates a choice expression of all exported rules in the given provider.
     * 
     * this expression is used as a pseudo content model of anyOtherElement.
     */
    private Expression createChoiceOfAllExportedRules( SchemaProvider provider ) {
        Expression exp = Expression.nullSet;
        
        Iterator itr = provider.iterateNamespace();
        while( itr.hasNext() ) {
            String namespace = (String)itr.next();
            IslandSchema is = provider.getSchemaByNamespace(namespace);
            ElementDecl[] rules = is.getElementDecls();
            
            for( int j=0; j<rules.length; j++ )
                exp = module.pool.createChoice(exp,
                    new ExternalElementExp(module.pool,namespace,rules[j].getName(),null));
        }
        
        return exp;
    }
}
