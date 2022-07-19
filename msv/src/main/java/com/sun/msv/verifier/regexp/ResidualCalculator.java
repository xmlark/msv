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

package com.sun.msv.verifier.regexp;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.ExpressionVisitorExpression;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * computes regular expression derivative.
 * 
 * this class receives a regexp and computes the right language after eating
 * given token.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ResidualCalculator implements ExpressionVisitorExpression {
    protected Token token;
    protected final ExpressionPool pool;
    
    public ResidualCalculator( ExpressionPool pool )    { this.pool=pool; }
    
    /** compute the residual */
    final Expression calcResidual( Expression exp, ElementToken token ) {
        if( token.acceptedPatterns!=null && token.acceptedPatterns.length==1 ) {
            // we can use optimization table
            OptimizationTag ot;
            if(exp.verifierTag==null)
                exp.verifierTag = ot = new OptimizationTag();
            else {
                ot = (OptimizationTag)exp.verifierTag;
                Expression residual = (Expression)ot.simpleElementTokenResidual.get(token.acceptedPatterns[0]);
                if(residual!=null)
                    return residual;    // cache hit.
            }
            
            this.token = token;
            Expression residual = exp.visit(this);
            // cache this residual
            ot.simpleElementTokenResidual.put(token.acceptedPatterns[0],residual);
            return residual;
        }
        
        // no chance of using cache.
        this.token = token;
        return exp.visit(this);
    }
    
    /** compute the residual */
    public final Expression calcResidual( Expression exp, Token token ) {
        if( token instanceof ElementToken )
            return calcResidual(exp,(ElementToken)token);
        
        this.token=token;
        Expression r = exp.visit(this);

        // if token is ignorable, make expression as so.
        if( token.isIgnorable() )
            r = pool.createChoice(r,exp);
        
        return r;
    }
    
    public Expression onAttribute( AttributeExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                return Expression.nullSet;
    }
    public Expression onChoice( ChoiceExp exp ) {
        return pool.createChoice( exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onElement( ElementExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                return Expression.nullSet;
    }
    public Expression onOneOrMore( OneOrMoreExp exp ) {
        return pool.createSequence(
            exp.exp.visit(this),
            pool.createZeroOrMore(exp.exp) );
    }
    public Expression onMixed( MixedExp exp ) {
        // if token can be interpreted as anyString, eat it.
        if( token.matchAnyString() )    return exp;
        
        // otherwise, it must be consumed by the children.
        return pool.createMixed( exp.exp.visit(this) );
    }
    public Expression onEpsilon() {
        return Expression.nullSet;
    }
    public Expression onNullSet() {
        return Expression.nullSet;
    }
    public Expression onAnyString() {
        // anyString is not reduced to epsilon.
        // it remains there even after consuming StringToken.
        if(token.matchAnyString())        return Expression.anyString;
        else                            return Expression.nullSet;
    }
    public Expression onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onSequence( SequenceExp exp ) {
        Expression r = pool.createSequence( exp.exp1.visit(this), exp.exp2 );
        
        if( exp.exp1.isEpsilonReducible() )
            return pool.createChoice( r, exp.exp2.visit(this) );
        else
            return r;
    }
    
    public Expression onData( DataExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onValue( ValueExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onList( ListExp exp ) {
        if(token.match(exp))    return Expression.epsilon;
        else                    return Expression.nullSet;
    }
    
    public Expression onConcur( ConcurExp exp ) {
        return pool.createConcur(
            exp.exp1.visit(this), exp.exp2.visit(this) );
    }
    public Expression onInterleave( InterleaveExp exp ) {
        return pool.createChoice(
            pool.createInterleave( exp.exp1.visit(this), exp.exp2 ),
            pool.createInterleave( exp.exp1, exp.exp2.visit(this) ) );
    }
}
