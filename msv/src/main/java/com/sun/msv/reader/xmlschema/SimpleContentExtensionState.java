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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.grammar.xmlschema.SimpleTypeExp;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse extension element as a child of &lt;simpleContent&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleContentExtensionState extends SequenceState
    implements AnyAttributeOwner {
    
    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;
    
    protected SimpleContentExtensionState( ComplexTypeExp parentDecl ) {
        this.parentDecl = parentDecl;
    }

    public void setAttributeWildcard( AttributeWildcard local ) {
        parentDecl.wildcard = local;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createAttributeState(this,tag);
    }
    
    
    protected Expression initialExpression() {
        // without this statement,
        // <extension> without any attribute will be prohibited.
        return Expression.epsilon;
    }
    
    protected Expression annealExpression( Expression exp ) {
        parentDecl.derivationMethod = ComplexTypeExp.EXTENSION;
        return reader.pool.createSequence(
            super.annealExpression(exp),
            getBody());
    }
    
    /**
     * Gets the expression for the base type.
     */
    private Expression getBody() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        final String base = startTag.getAttribute("base");
        if(base==null) {
            // in extension, base attribute must is mandatory.
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, startTag.localName, "base");
            return Expression.nullSet;
        }
        
        final String[] baseTypeName = reader.splitQName(base);
        if( baseTypeName==null ) {
            reader.reportError( XMLSchemaReader.ERR_UNDECLARED_PREFIX, base );
            return Expression.nullSet;
        }
        
        // we need a special handling for built-in types
        if(reader.isSchemaNamespace(baseTypeName[0])) {
            XSDatatype dt = reader.resolveBuiltinDataType(baseTypeName[1]);
            if(dt!=null) {
                XSDatatypeExp dtexp = new XSDatatypeExp(dt,reader.pool);
                parentDecl.simpleBaseType = dtexp;
                return dtexp;
            }
            
            // maybe we are parsing the schema for schema.
            // consult externally specified schema.
        }
        
        final XMLSchemaSchema schema = reader.grammar.getByNamespace(baseTypeName[0]);
        
        // we don't know whether it's a complex type or a simple type.
        // so back patch it
        final ReferenceExp ref = new ReferenceExp(null);
        reader.addBackPatchJob( new GrammarReader.BackPatch(){
            public State getOwnerState() {
                return SimpleContentExtensionState.this;
            }
            public void patch() {
                SimpleTypeExp sexp = schema.simpleTypes.get(baseTypeName[1]);
                if(sexp!=null) {
                    // we've found the simple type
                    ref.exp = sexp;
                    parentDecl.simpleBaseType = sexp.getType();
                    _assert(parentDecl.simpleBaseType!=null);
                    return;
                }
                ComplexTypeExp cexp = schema.complexTypes.get(baseTypeName[1]);
                if(cexp!=null) {
                    // we've found the complex type as the base type
                    ref.exp = cexp.body;
                    parentDecl.complexBaseType = cexp;
                    return;
                }
                
                // there is no base type.
                reader.reportError( XMLSchemaReader.ERR_UNDEFINED_COMPLEX_OR_SIMPLE_TYPE, base );
            }
        });
        
        return ref;
    }
}
