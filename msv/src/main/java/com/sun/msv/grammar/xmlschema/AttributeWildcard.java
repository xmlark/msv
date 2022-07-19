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

package com.sun.msv.grammar.xmlschema;

import java.util.Iterator;

import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.NameClass;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Attribute wildcard property of the schema component.
 * 
 * <p>
 * This object is used during the parsing process to keep the intermediate information.
 * Once the parsing is finished, attribute wildcard is kept as an expression.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeWildcard {
    
    public AttributeWildcard( NameClass name, int processMode ) {
        this.name = name;
        this.processMode = processMode;
    }
    
    private NameClass name;
    
    /** Gets the target of the name class. */
    public NameClass getName() { return name; }
    
    private int processMode;
    
    /** Gets the processing model of the wildcard. */
    public int getProcessMode() { return processMode; }
    
    public static final int SKIP    = 0;
    public static final int LAX        = 1;
    public static final int STRICT    = 2;
    
    public AttributeWildcard copy() {
        return new AttributeWildcard(name,processMode);
    }
    
    /**
     * Creates the expression that corresponds to
     * the current attribute wildcard specification.
     */
    public Expression createExpression( XMLSchemaGrammar grammar ) {
        final ExpressionPool pool = grammar.pool;
        
        switch(processMode) {
        case SKIP:
            return pool.createZeroOrMore(pool.createAttribute(name));
            
        case STRICT:
        case LAX:
            
            Expression exp = Expression.epsilon;
            LaxDefaultNameClass laxNc = new LaxDefaultNameClass(name);
            
            Iterator itr = grammar.iterateSchemas();
            while( itr.hasNext() ) {
                XMLSchemaSchema schema = (XMLSchemaSchema)itr.next();
                // nc is built by using NamespaceNameClass.
                // "strict" allows global element declarations of 
                // specified namespaces.
                if(name.accepts( schema.targetNamespace, NameClass.LOCALNAME_WILDCARD )) {
                    
                    // gather global attributes.
                    ReferenceExp[] atts = schema.attributeDecls.getAll();
                    for( int i=0; i<atts.length; i++ ) {
                        exp = pool.createSequence( pool.createOptional(atts[i]), exp );
                        laxNc.addName( schema.targetNamespace, atts[i].name );
                    }
                }
            }
                
            if( processMode==STRICT )
                // if processContents="strict", then that's it.
                return exp;
                
            // if "lax", we have to add an expression to
            // match other attributes.
            return pool.createSequence(
                pool.createZeroOrMore(pool.createAttribute(laxNc)), exp );
        
        default:
            throw new Error("undefined process mode:"+processMode);
        }
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
