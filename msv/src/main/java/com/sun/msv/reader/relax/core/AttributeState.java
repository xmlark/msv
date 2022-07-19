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

package com.sun.msv.reader.relax.core;

import org.relaxng.datatype.DatatypeException;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.SimpleNameClass;
import com.sun.msv.reader.ExpressionState;
import com.sun.msv.reader.State;
import com.sun.msv.reader.datatype.xsd.FacetStateParent;
import com.sun.msv.reader.datatype.xsd.XSTypeIncubator;
import com.sun.msv.util.StartTagInfo;

/**
 * parses &lt;attribute&gt; element.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeState extends ExpressionState implements FacetStateParent
{
    protected XSTypeIncubator incubator;
    
    public XSTypeIncubator getIncubator() { return incubator; }
    
    protected void startSelf() {
        super.startSelf();
        
        final RELAXCoreReader reader = (RELAXCoreReader)this.reader;
        
        String type        = startTag.getAttribute("type");
        if(type==null)    type="string";
        incubator = reader.resolveXSDatatype(type).createIncubator();
    }
    
    protected Expression makeExpression() {
        try    {
            final String name        = startTag.getAttribute("name");
            final String required    = startTag.getAttribute("required");
            
            if( name==null ) {
                reader.reportError( RELAXCoreReader.ERR_MISSING_ATTRIBUTE, "attribute","name" );
                // recover by ignoring this attribute.
                // since attributes are combined by sequence, so epsilon is appropriate.
                return Expression.epsilon;
            }
            
            Expression exp = reader.pool.createAttribute(
                new SimpleNameClass("",name),
                incubator.derive(null,null) );
            
            // unless required attribute is specified, it is considered optional
            if(! "true".equals(required) )
                exp = reader.pool.createOptional(exp);
            
            return exp;
        } catch( DatatypeException e ) {
            // derivation failed
            reader.reportError( e, RELAXCoreReader.ERR_BAD_TYPE, e.getMessage() );
            // recover by using harmless expression. anything will do.
            return Expression.anyString;
        }
    }
    
    protected State createChildState( StartTagInfo tag ) {
        return ((RELAXCoreReader)reader).createFacetState(this,tag);    // facets
    }
}
