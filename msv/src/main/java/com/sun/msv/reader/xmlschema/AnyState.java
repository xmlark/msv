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
