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
