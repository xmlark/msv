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

package com.sun.msv.grammar;

/**
 * Attribute declaration.
 * 
 * <p>
 * Attribute declaration consists of a NameClass that verifies attribute name
 * and an Expression that verifies the value of the attribute.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributeExp extends Expression implements NameClassAndExpression {
    
    /** constraint over attribute name */
    public final NameClass nameClass;
    public final NameClass getNameClass() { return nameClass; }
    
    /** child expression */
    public final Expression exp;
    public final Expression getContentModel() { return exp; }
    
    public AttributeExp( NameClass nameClass, Expression exp ) {
        super( nameClass.hashCode()+exp.hashCode() );
        this.nameClass    = nameClass;
        this.exp        = exp;
    }

    protected final int calcHashCode() {
        return nameClass.hashCode()+exp.hashCode();
    }
    
    public boolean equals( Object o ) {
        // reject derived classes
        if(o.getClass()!=AttributeExp.class)    return false;
        
        AttributeExp rhs = (AttributeExp)o;
        return rhs.nameClass.equals(nameClass) && rhs.exp.equals(exp);
    }
    
    public Object visit( ExpressionVisitor visitor )                { return visitor.onAttribute(this);    }
    public Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onAttribute(this); }
    public boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onAttribute(this);    }
    public void visit( ExpressionVisitorVoid visitor )                { visitor.onAttribute(this);    }
    
    protected boolean calcEpsilonReducibility() {
        return false;
    }

    // serialization support
    private static final long serialVersionUID = 1;    
}
