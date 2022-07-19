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
 * removes all unnecessary expressions and
 * creates an expression that consists of required attributes and choices only.
 * 
 * <XMP>
 * For example,
 * 
 * <choice>
 *   <element />
 *   <attribute />
 * </choice>
 * 
 * will be converted to
 * 
 * <empty />
 * 
 * because no attribute is required. But
 * 
 * <choice>
 *   <attribute />
 *   <attribute />
 * </choice>
 * 
 * will remain the same because one or the other is required.
 * 
 * this method also removes SequenceExp.
 * 
 * <sequence>
 *   <attribute name="A" />
 *   <attribute name="B" />
 * </sequence>
 * 
 * will be converted to
 * 
 * <attribute name="A" />
 * 
 * This function object is used only for error recovery.
 * Resulting expressions always consist only of <choice>s and <attribute>s.
 * </XMP>
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributePicker implements ExpressionVisitorExpression
{
    private final ExpressionPool pool;
    
    public AttributePicker( ExpressionPool pool ) {
        this.pool = pool;
    }
    
    public Expression onElement( ElementExp exp ) {
        return AttributeExp.epsilon;
    }
    
    public Expression onMixed( MixedExp exp ) {
        return exp.exp.visit(this);
    }
    
    public Expression onAnyString() {
        return Expression.epsilon;
    }
    
    public Expression onEpsilon() {
        return Expression.epsilon;
    }
    
    public Expression onNullSet() {
        return Expression.nullSet;
    }
    
    public Expression onRef( ReferenceExp exp ) {
        return exp.exp.visit(this);
    }
    public Expression onOther( OtherExp exp ) {
        return exp.exp.visit(this);
    }
    
    public Expression onData( DataExp exp ) {
        return Expression.epsilon;
    }
    public Expression onValue( ValueExp exp ) {
        return Expression.epsilon;
    }

    public Expression onList( ListExp exp ) {
        return Expression.epsilon;
    }

    public Expression onAttribute( AttributeExp exp ) {
        return exp;
    }
    
    public Expression onOneOrMore( OneOrMoreExp exp ) {
        // reduce A+ -> A
        return exp.exp.visit(this);
    }
    
    public Expression onSequence( SequenceExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        
        if(ex1.isEpsilonReducible()) {
            if(ex2.isEpsilonReducible())    return Expression.epsilon;
            else                            return ex2;
        }
        else
            return ex1;
    }
    
    public Expression onInterleave( InterleaveExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        
        if(ex1.isEpsilonReducible()) {
            if(ex2.isEpsilonReducible())    return Expression.epsilon;
            else                            return ex2;
        } else
            return ex1;
    }
    
    public Expression onConcur( ConcurExp exp ) {
        // abandon concur.
        return Expression.epsilon;
    }
    
    public Expression onChoice( ChoiceExp exp ) {
        Expression ex1 = exp.exp1.visit(this);
        Expression ex2 = exp.exp2.visit(this);
        // if one of choice is epsilon-reducible,
        // the entire choice becomes optional.
        // optional attributes have to be removed from the result.
        if( ex1.isEpsilonReducible() || ex2.isEpsilonReducible() )
            return Expression.epsilon;
        return pool.createChoice(ex1,ex2);
    }
}
