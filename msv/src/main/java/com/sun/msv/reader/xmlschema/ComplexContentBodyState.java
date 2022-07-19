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
