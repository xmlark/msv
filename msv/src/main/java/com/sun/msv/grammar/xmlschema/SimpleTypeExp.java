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
