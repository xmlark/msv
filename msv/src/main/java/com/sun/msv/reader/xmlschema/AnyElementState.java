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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.trex.ElementPattern;
import com.sun.msv.grammar.xmlschema.ElementDeclExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.State;

/**
 * used to parse &lt;any &gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyElementState extends AnyState
{
    protected Expression createExpression( final String namespace, final String process ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        final XMLSchemaSchema currentSchema = reader.currentSchema;
        
        if( process.equals("skip") ) {
            // "skip" can be expanded now.
            NameClass nc = getNameClass(namespace,currentSchema);
            
            ElementPattern ep = new ElementPattern(nc,Expression.nullSet);
                
            ep.contentModel = 
                // <mixed><zeroOrMore><choice><attribute /><element /></choice></zeroOrMore></mixed>
                reader.pool.createMixed(
                    reader.pool.createZeroOrMore(
                        reader.pool.createChoice(
                            ep,
                            reader.pool.createAttribute(nc)
                        )
                    )
                );
                
            // minOccurs/maxOccurs is processed through interception
            return ep;
        }
        
        // "lax"/"strict" has to be back-patched later.
        final ReferenceExp exp = new ReferenceExp("any("+process+":"+namespace+")");
        reader.addBackPatchJob( new GrammarReader.BackPatch(){
            public State getOwnerState() { return AnyElementState.this; }
            public void patch() {

                if( !process.equals("lax")
                &&  !process.equals("strict") )  {
                    reader.reportError( XMLSchemaReader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process );
                    exp.exp = Expression.nullSet;
                    return;
                }
                
                exp.exp = Expression.nullSet;
                NameClass nc = getNameClass(namespace,currentSchema);
                Iterator itr;
                itr = reader.grammar.iterateSchemas();
                while( itr.hasNext() ) {
                    XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
                    // nc is built by using NamespaceNameClass.
                    // "strict" allows global element declarations of 
                    // specified namespaces.
                    if(nc.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD ))
                        // schema.topLevel is choices of globally declared elements.
                        exp.exp = reader.pool.createChoice( exp.exp, schema.topLevel );
                }
                
                if( !process.equals("lax") )
                    return;    // if processContents="strict", the above is fine.
                
                // if "lax", we have to add an expression to
                // match other elements.
                NameClass laxNc = createLaxNameClass( nc,
                    new XMLSchemaReader.RefResolver() {
                        public ReferenceContainer get( XMLSchemaSchema schema ) {
                            return schema.elementDecls;
                        }
                    });
                
                exp.exp = reader.pool.createChoice(
                    new ElementPattern( laxNc,
                        reader.pool.createMixed(
                            reader.pool.createZeroOrMore(
                                reader.pool.createChoice(
                                    reader.pool.createAttribute(NameClass.ALL),
                                    exp)))),
                     exp.exp );
            }
        });
        
        exp.exp = Expression.nullSet;    // dummy for a while.
        
        // minOccurs/maxOccurs is processed through interception
        return exp;
    }

    protected NameClass getNameClassFrom( ReferenceExp exp ) {
        return ((ElementDeclExp)exp).getElementExp().getNameClass();
    }
    
}
