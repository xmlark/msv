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

/**
 * attribute group declaration.
 * 
 * the inherited exp field contains the attributes defined in this declaration.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeGroupExp extends RedefinableExp implements AttWildcardExp {
    
    /**
     * Attribute wild card constraint.
     * 
     * <p>
     * Due to the nasty definition of the interaction between attribute wildcards,
     * we cannot add the expression for validating wildcard until the very last moment.
     * 
     * <p>
     * In any way, <code>AttribtueGroupExp</code> will NOT contain the expression
     * corresponding to the wildcard. Only <code>ComplexTypeExp</code> will get 
     * that expression.
     * 
     * <p>
     * Until the wrap-up phase of the schema parsing, this field will contain
     * the "local wildcard definition." In the wrap-up phase, this field is replaced
     * by the "complete wildcard definition." 
     */
    public AttributeWildcard wildcard;
    
    public AttributeWildcard getAttributeWildcard() { return wildcard; }
    public void setAttributeWildcard( AttributeWildcard local ) { wildcard=local; }
    
    /**
     * name of this attribute group declaration.
     * According to the spec, the name must be unique within one schema
     * (in our object model, one XMLSchemaSchema object).
     */
    public AttributeGroupExp( String typeLocalName ) {
        super(typeLocalName);
    }
    
    /** clone this object. */
    public RedefinableExp getClone() {
        RedefinableExp exp = new AttributeGroupExp(super.name);
        exp.redefine(this);
        return exp;
    }

// this class does not have its own member, so no need to override this method.
    public void redefine( RedefinableExp _rhs ) {
        super.redefine(_rhs);
        
        AttributeGroupExp rhs = (AttributeGroupExp)_rhs;
        if(rhs.wildcard==null)    wildcard = null;
        else                    wildcard = rhs.wildcard.copy();
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
