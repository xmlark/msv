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
