/*
 * @(#)$Id$
 *
 * Copyright 2001 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
        super( hashCode( nameClass, exp, HASHCODE_ATTRIBUTE ) );
        this.nameClass    = nameClass;
        this.exp        = exp;
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
