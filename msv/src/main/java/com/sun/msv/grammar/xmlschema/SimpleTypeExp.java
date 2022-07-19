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

import com.sun.msv.datatype.xsd.XSDatatype;
import com.sun.msv.reader.datatype.xsd.XSDatatypeExp;

/**
 * Simple type declaration.
 * 
 * <p>
 * Most of the properties of the simple type declaration component
 * is defined in the {@link XSDatatype} object, which is obtained by the
 * {@link #getType()} method.
 * 
 * <p>
 * Note: XML Schema allows forward reference to simple types.
 * Therefore it must be indirectionalized by ReferenceExp.
 * And this is the only reason this class exists.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class SimpleTypeExp extends XMLSchemaTypeExp {
    
    SimpleTypeExp( String typeName ) {
        super(typeName);
    }
    
    public void set( XSDatatypeExp exp ) {
        this.exp = this.type = exp;
    }
    
    protected XSDatatypeExp type;
    /** gets the XSDatatypeExp object that represents this simple type. */
    public XSDatatypeExp getType() { return type; }

    /**
     * Gets the encapsulated Datatype object.
     * <p>
     * This method can be called only after the parsing is finished.
     */
    public XSDatatype getDatatype() { return type.getCreatedType(); }
    
    
    /**
     * gets the value of the block constraint.
     * SimpleTypeExp always returns 0 because it doesn't
     * have the block constraint.
     */
    public int getBlock() { return 0; }
    
    /** clone this object. */
    public RedefinableExp getClone() {
        SimpleTypeExp exp = new SimpleTypeExp(this.name);
        exp.redefine(this);
        return exp;
    }
    
    public void redefine( RedefinableExp _rhs ) {
        super.redefine(_rhs);
        
        SimpleTypeExp rhs = (SimpleTypeExp)_rhs;
        
        if(type==null)
            type = rhs.getType().getClone();
        else {
            // because redefinition only happens by a defined declaration
            if(rhs.getType()==null)
                throw new InternalError();
                
            type.redefine(rhs.getType());
        }
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    

}
