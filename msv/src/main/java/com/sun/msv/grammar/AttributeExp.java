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
