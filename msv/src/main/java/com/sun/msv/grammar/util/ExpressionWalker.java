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

package com.sun.msv.grammar.util;

import com.sun.msv.grammar.AttributeExp;
import com.sun.msv.grammar.BinaryExp;
import com.sun.msv.grammar.ChoiceExp;
import com.sun.msv.grammar.ConcurExp;
import com.sun.msv.grammar.DataExp;
import com.sun.msv.grammar.ElementExp;
import com.sun.msv.grammar.ExpressionVisitorVoid;
import com.sun.msv.grammar.InterleaveExp;
import com.sun.msv.grammar.ListExp;
import com.sun.msv.grammar.MixedExp;
import com.sun.msv.grammar.OneOrMoreExp;
import com.sun.msv.grammar.OtherExp;
import com.sun.msv.grammar.ReferenceExp;
import com.sun.msv.grammar.SequenceExp;
import com.sun.msv.grammar.ValueExp;

/**
 * Visits all reachable expressions but do nothing.
 * 
 * Note that unless the derived class do something, this implementation
 * will recurse infinitely.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class ExpressionWalker implements ExpressionVisitorVoid {
        
    public void onRef( ReferenceExp exp ) {
        exp.exp.visit(this);
    }
        
    public void onOther( OtherExp exp ) {
        exp.exp.visit(this);
    }
    
    public void onElement( ElementExp exp ) {
        exp.contentModel.visit(this);
    }
        
    public void onEpsilon() {}
    public void onNullSet() {}
    public void onAnyString() {}
    public void onData( DataExp exp ) {}
    public void onValue( ValueExp exp ) {}
        
    public void onInterleave( InterleaveExp exp ) {
        onBinExp(exp);
    }
        
    public void onConcur( ConcurExp exp ) {
        onBinExp(exp);
    }
            
    public void onChoice( ChoiceExp exp ) {
        onBinExp(exp);
    }
        
    public void onSequence( SequenceExp exp ) {
        onBinExp(exp);
    }
        
    public void onBinExp( BinaryExp exp ) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
    }
        
    public void onMixed( MixedExp exp ) {
        exp.exp.visit(this);
    }
    
    public void onList( ListExp exp ) {
        exp.exp.visit(this);
    }
    
    public void onOneOrMore( OneOrMoreExp exp ) {
        exp.exp.visit(this);
    }
        
    public void onAttribute( AttributeExp exp ) {
        exp.exp.visit(this);
    }
}
