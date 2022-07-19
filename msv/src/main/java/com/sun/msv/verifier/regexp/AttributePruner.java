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
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.Expression;
import com.sun.msv.grammar.ExpressionCloner;
import com.sun.msv.grammar.ExpressionPool;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;

/**
 * Creates an expression whose AttributeExp is completely replaced by nullSet.
 * 
 * This step is used to remove all unconsumed AttributeExp from the expression.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AttributePruner extends ExpressionCloner
{
    public AttributePruner( ExpressionPool pool ) { super(pool); }
    
    public Expression onAttribute( AttributeExp exp )    { return Expression.nullSet; }
    public Expression onRef( ReferenceExp exp )            { return exp.exp.visit(this); }
    public Expression onOther( OtherExp exp )            { return exp.exp.visit(this); }
    public Expression onElement( ElementExp exp )        { return exp; }
    
    public final Expression prune( Expression exp ) {
        // check the cache first.
        OptimizationTag ot = (OptimizationTag)exp.verifierTag;
        if(ot==null)    exp.verifierTag = ot = new OptimizationTag();
        else
            if( ot.attributePrunedExpression!=null )
                return ot.attributePrunedExpression;
        
        // cache miss. compute it.
        Expression r = exp.visit(this);
        
        ot.attributePrunedExpression = r;    // cache this result
        return r;
    }
}
