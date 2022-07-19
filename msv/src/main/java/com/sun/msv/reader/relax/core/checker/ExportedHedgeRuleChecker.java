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

package com.sun.msv.reader.relax.core.checker;

import java.util.Stack;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;
import com.sun.msv.grammar.relax.AttPoolClause;
import com.sun.msv.grammar.relax.ElementRules;
import com.sun.msv.grammar.relax.HedgeRules;
import com.sun.msv.grammar.relax.RELAXExpressionVisitorBoolean;
import com.sun.msv.grammar.relax.RELAXModule;
import com.sun.msv.grammar.relax.TagClause;

/**
 * the purpose of this function object is to make sure
 * that the expression does not contain references to modules
 * other than one specified by this variable.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public final class ExportedHedgeRuleChecker implements RELAXExpressionVisitorBoolean
{
    private final RELAXModule module;
    public ExportedHedgeRuleChecker( RELAXModule module ) { this.module = module; }
    
    /**
     * traversal stack.
     * 
     * This object keeps track of how hedgeRules are visited so that
     * detailed error message can be provided when an error is found.
     * 
     * Say if you start from hr1, hr1 refers hr2, hr2 refers hr3,
     * and hr3 has a reference to the other module, this stack is
     * {hr1,hr2,hr3} when an error is found. 
     */
    private final Stack traversalStack = new Stack();
    
    public ReferenceExp[] errorSnapshot = null;
    
    public boolean onAttribute( AttributeExp exp )        { return true; }
    public boolean onChoice( ChoiceExp exp )            { return exp.exp1.visit(this) && exp.exp2.visit(this); }
    public boolean onSequence( SequenceExp exp )        { return exp.exp1.visit(this) && exp.exp2.visit(this); }
    public boolean onElement( ElementExp exp )            { return true; }
    public boolean onOneOrMore( OneOrMoreExp exp )        { return exp.exp.visit(this); }
    public boolean onMixed( MixedExp exp )                { return exp.exp.visit(this); }
    public boolean onRef( ReferenceExp exp )            { throw new Error(); }    // should never be called
    public boolean onOther( OtherExp exp )                { return exp.exp.visit(this); }
    public boolean onEpsilon()                            { return true; }
    public boolean onNullSet()                            { return true; }
    public boolean onAnyString()                        { return true; }
    public boolean onData( DataExp exp )                { return true; }
    public boolean onValue( ValueExp exp )                { return true; }
    public boolean onAttPool( AttPoolClause exp )        { throw new Error(); }    // should never be called
    public boolean onTag( TagClause exp )                { throw new Error(); }    // should never be called

    // these two shall never be called in case of RELAX.
    public boolean onInterleave( InterleaveExp exp )    { throw new Error(); }
    public boolean onConcur( ConcurExp exp )            { throw new Error(); }
    public boolean onList( ListExp exp )                { throw new Error(); }
    
    public boolean onElementRules( ElementRules exp ) {
        if(exp.ownerModule==module)        return true;
        
        takeSnapshot(exp);
        return false;
    }
    public boolean onHedgeRules( HedgeRules exp ) {
        if( exp.ownerModule!=module ) {    // reference to the other namespace
            takeSnapshot(exp);
            return false;
        }
        
        traversalStack.push(exp);
        // we have to make sure the same thing for this referenced hedgeRule.
        boolean r = exp.exp.visit(this);
        traversalStack.pop();
        return r;
    }
    
    /**
     * takes a snap shot of traversal to this.errorSnapshot
     * so that the user will know what references cause this problem.
     */
    private void takeSnapshot( ReferenceExp lastExp ) {
        errorSnapshot = new ReferenceExp[ traversalStack.size()+1 ];
        traversalStack.toArray(errorSnapshot);
        errorSnapshot[errorSnapshot.length-1] = lastExp;
    }
}
