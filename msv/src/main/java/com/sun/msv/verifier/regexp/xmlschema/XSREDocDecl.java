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

package com.sun.msv.verifier.regexp.xmlschema;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.NamespaceNameClass;
import com.sun.msv.grammar.xmlschema.XMLSchemaGrammar;
import com.sun.msv.verifier.Acceptor;
import com.sun.msv.verifier.identity.IDConstraintChecker;
import com.sun.msv.verifier.regexp.AttributeFeeder;
import com.sun.msv.verifier.regexp.CombinedChildContentExpCreator;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * {@link REDocumentDeclaration} that supports several W3C XML Schema
 * specific semantics.
 * 
 * <p>
 * If you do validation by using W3C XML Schema, then you should use
 * this VGM instead of plain <code>REDocumentDeclaration</code>.
 * You should also use {@link IDConstraintChecker} instead of Verifier class.
 * 
 * <p>
 * This package implements the following things:
 * <ol>
 *  <li>the xsi:nil attribute support.
 *  <li>the runtime type substitution by the xsi:type attribute
 * </ol>
 */
public class XSREDocDecl extends REDocumentDeclaration {

    public XSREDocDecl( XMLSchemaGrammar grammar ) {
        super(grammar);
        this.grammar = grammar;
    }

    public Acceptor createAcceptor() {
        // use XSAcceptor instead
        return new XSAcceptor(this, topLevel, null, Expression.epsilon);
    }

    CombinedChildContentExpCreator getCCCEC() { return super.cccec; }
    AttributeFeeder getAttFeeder() { return super.attFeeder; }
    
    /**
     * the grammar which this VGM is using.
     * 
     * For one, this object is used to find the complex type definition
     * by its name.
     */
    final protected XMLSchemaGrammar grammar;
    
    /**
     * AttributeExp that matches to "xsi:***" attributes.
     */
    final protected AttributeExp xsiAttExp =
        new AttributeExp(
            new NamespaceNameClass(XSAcceptor.XSINamespace),
            Expression.anyString);
    
    public String localizeMessage( String propertyName, Object[] args ) {
        try {
            String format = java.util.ResourceBundle.getBundle(
                "com.sun.msv.verifier.regexp.xmlschema.Messages").getString(propertyName);
        
            return java.text.MessageFormat.format(format, args );
        } catch( Exception e ) {
            return super.localizeMessage(propertyName,args);
        }
    }
    
    public static final String ERR_NON_NILLABLE_ELEMENT = // arg:1
        "XMLSchemaVerifier.NonNillableElement";
    public static final String ERR_NOT_SUBSTITUTABLE_TYPE = // arg:1
        "XMLSchemaVerifier.NotSubstitutableType";
    public static final String ERR_UNDEFINED_TYPE = // arg:1
        "XMLSchemaVerifier.UndefinedType";

}
