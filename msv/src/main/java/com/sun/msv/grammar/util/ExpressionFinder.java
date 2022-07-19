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

package com.sun.msv.grammar.util;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitorBoolean;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Base class for "finding" something from an expression.
 * 
 * This class visits all reachable expressions and returns boolean.
 * 
 * In any binary expression, if one branch returns true, then the binary
 * expression itself returns true. Thus it can be used to find something
 * from an expression.
 * 
 * Note that unless the derived class do something, this implementation
 * will recurse infinitely.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionFinder implements ExpressionVisitorBoolean
{
    public boolean onSequence( SequenceExp exp )        { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onInterleave( InterleaveExp exp )    { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onConcur( ConcurExp exp )            { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onChoice( ChoiceExp exp )            { return exp.exp1.visit(this)||exp.exp2.visit(this); }
    public boolean onAttribute( AttributeExp exp )        { return exp.exp.visit(this); }
    public boolean onElement( ElementExp exp )            { return exp.contentModel.visit(this); }
    public boolean onOneOrMore( OneOrMoreExp exp )        { return exp.exp.visit(this); }
    public boolean onMixed( MixedExp exp )                { return exp.exp.visit(this); }
    public boolean onList( ListExp exp )                { return exp.exp.visit(this); }
    public boolean onRef( ReferenceExp exp )            { return exp.exp.visit(this); }
    public boolean onOther( OtherExp exp )                { return exp.exp.visit(this); }
    public boolean onEpsilon()                            { return false; }
    public boolean onNullSet()                            { return false; }
    public boolean onAnyString()                        { return false; }
    public boolean onData( DataExp exp )                { return false; }
    public boolean onValue( ValueExp exp )                { return false; }
}
