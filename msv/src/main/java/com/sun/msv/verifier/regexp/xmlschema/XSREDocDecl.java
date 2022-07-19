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
