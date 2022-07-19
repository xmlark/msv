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

import java.util.Iterator;

/**
 * Base implementation for those expression which has two child expressions.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BinaryExp extends Expression {
    
    public final Expression exp1;
    public final Expression exp2;
    
    public BinaryExp( Expression left, Expression right ) {
        super( left.hashCode()+right.hashCode() );
        this.exp1 = left;
        this.exp2 = right;
    }
    
    protected final int calcHashCode() {
        return exp1.hashCode()+exp2.hashCode();
    }
    
    public boolean equals( Object o ) {
        if( this.getClass()!=o.getClass() )        return false;
        
        // every existing children are already unified.
        // therefore, == is enough. (don't need to call equals)
        BinaryExp rhs = (BinaryExp)o;
        return rhs.exp1 == exp1
            && rhs.exp2 == exp2;
    }
    
    /**
     * returns all child expressions in one array.
     * 
     * This method is similar to the children method but it returns an array 
     * that contains all children instead of an iterator object.
     */
    public Expression[] getChildren() {
        // count the number of children
        int cnt=1;
        Expression exp = this;
        while( exp.getClass()==this.getClass() ) {
            cnt++;
            exp = ((BinaryExp)exp).exp1;
        }
        
        Expression[] r = new Expression[cnt];
        exp=this;
        while( exp.getClass()==this.getClass() ) {
            r[--cnt] = ((BinaryExp)exp).exp2;
            exp = ((BinaryExp)exp).exp1;
        }
        r[0] = exp;
        
        return r;
    }
    
    /**
     * iterates all child expressions.
     * 
     * Since expressions are binarized, expressions like A|B|C is modeled as
     * A|(B|C).  This is may not be preferable for some applications.
     * 
     * <P>
     * This method returns an iterator that iterates all children
     * (A,B, and C in this example)
     */
    public Iterator children() {
        final Expression[] items = getChildren();
        return new Iterator() {
            private int idx =0;
            
            public Object next() {
                return items[idx++];
            }
            public boolean hasNext() { return idx!=items.length; }
            public void remove() { throw new UnsupportedOperationException(); }
        };
    }
    
    
    // serialization support
    private static final long serialVersionUID = 1;    
}
