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

import org.xml.sax.Locator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ReferenceContainer;
import com.sun.msv.grammar.xmlschema.AttributeGroupExp;
import com.sun.msv.grammar.xmlschema.AttributeWildcard;
import com.sun.msv.grammar.xmlschema.XMLSchemaSchema;
import com.sun.msv.reader.State;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;attributeGroup /&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeGroupState extends RedefinableDeclState implements AnyAttributeOwner {
    
    protected State createChildState( StartTagInfo tag ) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        return reader.createAttributeState(this,tag);
    }

    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader)reader).currentSchema.attributeGroups;
    }
    
    protected Expression initialExpression() {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        String refQName = startTag.getAttribute("ref");
        if( refQName==null )
            // child expressions are expected. (although it's optional)
            return Expression.epsilon;

        Expression exp = reader.resolveQNameRef(
            startTag, "ref",
            new XMLSchemaReader.RefResolver() {
                public ReferenceContainer get( XMLSchemaSchema g ) {
                    return g.attributeGroups;
                }
            } );
        if( exp==null )        return Expression.epsilon;    // couldn't resolve QName.
        return exp;
    }

    private AttributeWildcard wildcard;
    public void setAttributeWildcard( AttributeWildcard local ) {
        this.wildcard = local;
    }
    
    protected Expression castExpression( Expression halfCastedExpression, Expression newChildExpression ) {
        if( startTag.containsAttribute("ref") )
            reader.reportError( XMLSchemaReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION );
        if( halfCastedExpression==null )
            return newChildExpression;    // the first one.
        return reader.pool.createSequence( newChildExpression, halfCastedExpression );
    }
    
    protected Expression annealExpression(Expression contentType) {
        final XMLSchemaReader reader = (XMLSchemaReader)this.reader;
        
        if( !isGlobal() )        return contentType;
        
        // if this is a global declaration register it.
        String name = startTag.getAttribute("name");
        if( name==null ) {
            reader.reportError( XMLSchemaReader.ERR_MISSING_ATTRIBUTE, "attributeGroup", "name" );
            return Expression.epsilon;
            // recover by returning something meaningless.
            // the parent state will ignore this.
        }
        AttributeGroupExp exp;
        if( isRedefine() )
            exp = (AttributeGroupExp)super.oldDecl;
        else {
            exp = reader.currentSchema.attributeGroups.getOrCreate(name);
            if( exp.exp!=null )
                reader.reportError( 
                    new Locator[]{this.location,reader.getDeclaredLocationOf(exp)},
                    XMLSchemaReader.ERR_DUPLICATE_ATTRIBUTE_GROUP_DEFINITION,
                    new Object[]{name} );
        }
        reader.setDeclaredLocationOf(exp);
        exp.exp = contentType;
        exp.wildcard = this.wildcard;
        return exp;
    }
}
