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

import org.relaxng.datatype.Datatype;

import com.sun.msv.util.StringPair;

/**
 * Expression that matchs a particular value of a {@link Datatype}.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ValueExp extends Expression implements DataOrValueExp {
    
    /** Datatype object that is used to test the equality. */
    public final Datatype dt;
    public Datatype getType() { return dt; }

    /** This expression matches this value only. */
    public final Object value;
    
    /**
     * name of this datatype.
     * 
     * The value of this field is not considered as significant.
     * When two TypedStringExps share the same Datatype object,
     * then they are unified even if they have different names.
     */
    public final StringPair name;
    public StringPair getName() { return name; }
    
    protected ValueExp( Datatype dt, StringPair typeName, Object value ) {
        super(dt.hashCode()+dt.valueHashCode(value));
        this.dt=dt;
        this.name = typeName;
        this.value = value;
    }

    protected final int calcHashCode() {
        return dt.hashCode()+dt.valueHashCode(value);
    }
    
    public boolean equals( Object o ) {
        // Note that equals method of this class *can* be sloppy, 
        // since this class does not have a pattern as its child.
        
        // Therefore datatype vocaburary does not necessarily provide
        // strict equals method.
        if(o.getClass()!=this.getClass())    return false;
        
        ValueExp rhs = (ValueExp)o;
        
        if(!rhs.dt.equals(dt))                return false;
        
        return dt.sameValue(value,rhs.value);
    }
    
    public Object visit( ExpressionVisitor visitor )                { return visitor.onValue(this); }
    public Expression visit( ExpressionVisitorExpression visitor )    { return visitor.onValue(this); }
    public boolean visit( ExpressionVisitorBoolean visitor )        { return visitor.onValue(this); }
    public void visit( ExpressionVisitorVoid visitor )                { visitor.onValue(this); }

    protected boolean calcEpsilonReducibility() {
        return false;
    }
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
