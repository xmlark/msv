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

package com.sun.msv.reader.xmlschema;

import java.util.Iterator;
import java.util.StringTokenizer;

import com.sun.msv.grammar.ChoiceNameClass;
import com.sun.msv.grammar.DifferenceNameClass;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.NotNameClass;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.xmlschema.LaxDefaultNameClass;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.ExpressionWithoutChildState;

/**
 * base implementation of AnyAttributeState and AnyElementState.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class AnyState extends ExpressionWithoutChildState {

    protected final Expression makeExpression() {
        return createExpression(
            startTag.getDefaultedAttribute("namespace","##any"),
            startTag.getDefaultedAttribute("processContents","strict") );
    }
    
    /**
     * creates AGM that corresponds to the specified parameters.
     */
    protected abstract Expression createExpression( String namespace, String process );
    
    /**
     * processes 'namepsace' attribute and gets corresponding NameClass object.
     */
    protected NameClass getNameClass( String namespace, XMLSchemaSchema currentSchema ) {
        // we have to get currentSchema through parameter because
        // this method is also used while back-patching, and 
        // reader.currentSchema points to the invalid schema in that case.
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        namespace = namespace.trim();
        
        if( namespace.equals("##any") )
            return NameClass.ALL;
        
        if( namespace.equals("##other") )
            // ##other means anything other than the target namespace and local.
            return new NotNameClass(
                new ChoiceNameClass(
                    new NamespaceNameClass(currentSchema.targetNamespace),
                    new NamespaceNameClass("")) );
        
        NameClass choices=null;
        
        StringTokenizer tokens = new StringTokenizer(namespace);
        while( tokens.hasMoreTokens() ) {
            String token = tokens.nextToken();
            
            NameClass nc;
            if( token.equals("##targetNamespace") )
                nc = new NamespaceNameClass(currentSchema.targetNamespace);
            else
            if( token.equals("##local") )
                nc = new NamespaceNameClass("");
            else
                nc = new NamespaceNameClass(token);
            
            if( choices==null )        choices = nc;
            else                    choices = new ChoiceNameClass(choices,nc);
        }
        
        if( choices==null ) {
            // no item was found.
            reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "namespace", namespace );
            return NameClass.ALL;
        }
        
        return choices;
    }
    
    protected abstract NameClass getNameClassFrom( ReferenceExp exp );
                    
    protected NameClass createLaxNameClass( NameClass allowedNc, XMLSchemaReader.RefResolver res ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        LaxDefaultNameClass laxNc = new LaxDefaultNameClass(allowedNc);
                
        Iterator itr = reader.grammar.iterateSchemas();
        while( itr.hasNext() ) {
            XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
            if(allowedNc.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD )) {
                ReferenceExp[] refs = res.get(schema).getAll();
                for( int i=0; i<refs.length; i++ ) {
                    NameClass name = getNameClassFrom(refs[i]);
                            
                    if(!(name instanceof SimpleNameClass ))
                        // assertion failed.
                        // XML Schema's element declaration is always simple name.
                        throw new Error();
                    SimpleNameClass snc = (SimpleNameClass)name;
                            
                    laxNc.addName(snc.namespaceURI,snc.localName);
                }
            }
        }

        // laxNc - names in namespaces that are not allowed.
        return new DifferenceNameClass( laxNc, new NotNameClass(allowedNc) );
    }
}
