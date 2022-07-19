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

package com.sun.msv.reader.relax.core;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRule;
import com.sun.msv.grammar.relax.TagClause;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;element&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class InlineElementState extends ExpressionState implements FacetStateParent {
    
    /** this field is set to null if this element has label attribute. */
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator() { return incubator; }
    
    protected State createChildState( StartTagInfo tag ) {
        if( incubator!=null )
            return ((RELAXCoreReader)reader).createFacetState(this,tag);    // facets
        else
            return null;    // nothing is allowed when @label is used.
    }
    
    protected void startSelf() {
        super.startSelf();
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        String type        = startTag.getAttribute("type");
        String label    = startTag.getAttribute("label");
        
        if( type!=null && label!=null )
            reader.reportError( RELAXCoreReader.ERR_CONFLICTING_ATTRIBUTES, "type", "label" );
            // recover by ignoring one attribute.
        
        if( type==null && label==null ) {
            reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE_2, "element", "type", "label" );
            type="string";
        }
        
        if( label!=null ) {
            incubator = null;
        } else {
            incubator = reader.resolveXSDatatype(type).createIncubator();
        }
    }
    
    protected Expression makeExpression() {
        try {
            final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
            final String name        = startTag.getAttribute("name");
            
            if( name==null ) {
                reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "element","name" );
                // recover by ignoring this element.
                return Expression.nullSet;
            }
            
            Expression contentModel;

            if( incubator!=null ) {
                contentModel = incubator.derive(null,null);
            } else {
                // @label is used
                String label = startTag.getAttribute("label");
                if(label==null)    throw new Error();
                
                contentModel = reader.module.hedgeRules.getOrCreate(label);
                reader.backwardReference.memorizeLink(contentModel);
            }
            
            TagClause c = new TagClause();
            c.nameClass = new SimpleNameClass( ((RELAXCoreReader)reader).module.targetNamespace, name );
            
            final String role = startTag.getAttribute("role");
            if( role==null )    c.exp = Expression.epsilon;    // no attribute
            else {
                // role attribute
                AttPoolClause att = reader.module.attPools.getOrCreate(role);
                c.exp = att;
                reader.backwardReference.memorizeLink(att);
            }
            
            // create anonymous ElementRule. this rule will never be added to
            // RefContainer.
            return new ElementRule( reader.pool, c, contentModel );
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.nullSet;
        }
    }
}
