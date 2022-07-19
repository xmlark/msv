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
 * clones an expression.
 * 
 * <p>
 * By visiting an expression, returns a cloned expression.
 * 
 * <p>
 * This class is useless by itself since expressions are shared and unified.
 * It should be used as a base class for various AGM-related tasks to modify
 * AGM.
 * 
 * <p>
 * Note that this class doesn't provide default implementations for
 * onAttribute, onElement, and onRef methods.
 * Typically, the derived class needs to do something to prevent infinite recursion.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionCloner implements ExpressionVisitorExpression {
    
    protected final ExpressionPool    pool;
        
    protected ExpressionCloner( ExpressionPool pool )    { this.pool = pool;    }
    
    public Expression onChoice( ChoiceExp exp ) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        if(exp.exp1==np1 && exp.exp2==np2)    return exp;
        else                                return pool.createChoice(np1,np2);
    }
    public Expression onOneOrMore( OneOrMoreExp exp ) {
        Expression np = exp.exp.visit(this);
        if(exp.exp==np)        return exp;
        else                return pool.createOneOrMore(np);
    }
    public Expression onMixed( MixedExp exp ) {
        Expression body = exp.exp.visit(this);
        if(exp.exp==body)        return exp;
        else                    return pool.createMixed( body );
    }
    public Expression onList( ListExp exp ) {
        Expression body = exp.exp.visit(this);
        if(exp.exp==body)        return exp;
        else                    return pool.createList( body );
    }
    public Expression onSequence( SequenceExp exp ) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        if(exp.exp1==np1 && exp.exp2==np2)    return exp;
        else                                return pool.createSequence(np1,np2);
    }
    public Expression onConcur( ConcurExp exp ) {
        return pool.createConcur(
            exp.exp1.visit(this), exp.exp2.visit(this));
    }
    public Expression onInterleave( InterleaveExp exp ) {
        return pool.createInterleave(
            exp.exp1.visit(this), exp.exp2.visit(this));
    }
    
            
    public Expression onEpsilon()    { return Expression.epsilon; }
    public Expression onNullSet()    { return Expression.nullSet; }
    public Expression onAnyString()    { return Expression.anyString; }
    public Expression onData( DataExp exp ) { return exp; }
    public Expression onValue( ValueExp exp ) { return exp; }
}
