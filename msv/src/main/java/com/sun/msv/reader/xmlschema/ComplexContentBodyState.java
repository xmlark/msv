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

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.ComplexTypeExp;
import com.sun.msv.reader.SequenceState;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * used to parse restriction/extension element as a child of complexContent element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ComplexContentBodyState extends SequenceState implements AnyAttributeOwner {
    
    /** ComplexType object that we are now constructing. */
    protected ComplexTypeExp parentDecl;
    
    /**
     * if this state is used to parse &lt;extension&gt;, then true.
     * if this state is used to parse &lt;restension&gt;, then false.
     */
    protected boolean extension;
    
    protected ComplexContentBodyState( ComplexTypeExp parentDecl, boolean extension ) {
        this.parentDecl = parentDecl;
        this.extension = extension;
    }
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        State s;
        if( super.exp==Expression.epsilon ) {
            // model group must be the first expression child.
            s = reader.createModelGroupState(this,tag);
            if(s!=null )    return s;
        }
        
        // attribute, attributeGroup, and anyAttribtue can be specified
        // after content model is given.
        return reader.createAttributeState(this,tag);
    }

    public void setAttributeWildcard( AttributeWildcard local ) {
        parentDecl.wildcard = local;
    }
    
    protected Expression initialExpression() {
        // if nothing is specified, then empty
        return Expression.epsilon;
    }
    
    protected Expression annealExpression( Expression exp ) {
        // hook this ComplexTypeExp into the base type's restrictions list.
        
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        exp = super.annealExpression(exp);
        
        
        String refQName = startTag.getAttribute("base");
        if( refQName==null ) {
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, startTag.qName, "base" );
            return exp;
            // recover by abandoning proper derivation processing
        }
        
        String[] r = reader.splitQName(refQName);
        if(r==null) {
            reader.reportError( XMLSchemaReader.ERR_UNDECLARED_PREFIX, refQName );
            return exp;
            // recover by abandoning proper derivation processing
        }
        
        if( reader.isSchemaNamespace(r[0]) && r[1].equals("anyType") )
            // derivation from anyType means this expression is the root of
            // derivation. So we don't have to connect this complex type to
            // the super class.
            return exp;
        
        ComplexTypeExp baseType =
            reader.getOrCreateSchema(r[0]/*uri*/).complexTypes.getOrCreate(r[1]/*local name*/);
        if( baseType==null )    return exp;    // recover by abandoning further processing of this declaration.
        
        reader.backwardReference.memorizeLink(baseType);
        
/*        if( extension )
            baseType.extensions.exp = reader.pool.createChoice(
                                            baseType.extensions.exp,
                                            parentDecl.selfWType );
            // actual content model will be <baseTypeContentModel>,<AddedContentModel>
        else
            baseType.restrictions.exp = reader.pool.createChoice(
                                            baseType.restrictions.exp,
                                            parentDecl.selfWType );
*/        
        // set other fields of the ComplexTypeExp.
        parentDecl.derivationMethod = extension?ComplexTypeExp.EXTENSION:ComplexTypeExp.RESTRICTION;
        parentDecl.complexBaseType = baseType;
        
        return combineToBaseType( baseType, exp );
    }

    /**
     * combines the base type content model and this content model
     */
    protected Expression combineToBaseType( ComplexTypeExp baseType, Expression addedExp ) {
        if( extension )
            return reader.pool.createSequence( baseType.body, addedExp );
        else
            return addedExp;
    }
}
